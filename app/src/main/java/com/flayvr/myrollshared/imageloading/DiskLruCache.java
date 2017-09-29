package com.flayvr.myrollshared.imageloading;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiskLruCache implements Closeable
{

    static final long ANY_SEQUENCE_NUMBER = -1L;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,64}");
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
        @Override
        public void write(int b) throws IOException {

        }
    };
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Callable cleanupCallable = new _cls1();
    private final File directory;
    final ThreadPoolExecutor executorService;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    private Writer journalWriter;
    private final LinkedHashMap<String, DiskLruCache.Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
    private long maxSize;
    private long nextSequenceNumber;
    private int redundantOpCount;
    private long size;
    private final int valueCount;

    private DiskLruCache(File file, int i, int j, long l)
    {
        size = 0L;
        nextSequenceNumber = 0L;
        executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        directory = file;
        appVersion = i;
        journalFile = new File(file, "journal");
        journalFileTmp = new File(file, "journal.tmp");
        journalFileBackup = new File(file, "journal.bkp");
        valueCount = j;
        maxSize = l;
    }

    private void checkNotClosed()
    {
        if(journalWriter == null)
        {
            throw new IllegalStateException("cache is closed");
        }
    }

    private void completeEdit(Editor editor, boolean flag)
    {
        Entry entry;
        long l;
        File file;
        File file1;
        long l1;
        long l2;
        try {
            synchronized (this){
                entry = editor.entry;
                if(entry.currentEditor != editor)
                {
                    throw new IllegalStateException();
                }
                int i = 0;
                if(flag) {
                    boolean flag1 = entry.readable;
                    if (!flag1) {
                        int j = 0;
                        int k = valueCount;
                        while (j < k) {
                            if (!editor.written[j]) {
                                editor.abort();
                                throw new IllegalStateException((new StringBuilder()).append("Newly created entry didn't create value for index ").append(j).toString());
                            }
                            if (!entry.getDirtyFile(j).exists()) {
                                editor.abort();
                                return;
                            }
                            j++;
                        }
                    }
                }
                while(i < valueCount)
                {
                    file = entry.getDirtyFile(i);
                    if(!flag)
                    {
                        deleteIfExists(file);
                    }else if(file.exists()) {
                        file1 = entry.getCleanFile(i);
                        file.renameTo(file1);
                        l1 = entry.lengths[i];
                        l2 = file1.length();
                        entry.lengths[i] = l2;
                        size = l2 + (size - l1);
                    }
                    i++;
                }
                redundantOpCount = 1 + redundantOpCount;
                entry.currentEditor = null;
                if (!(flag | entry.readable)) {
                    lruEntries.remove(entry.key);
                    journalWriter.write((new StringBuilder()).append("REMOVE ").append(entry.key).append('\n').toString());
                } else {
                    entry.readable = true;
                    journalWriter.write((new StringBuilder()).append("CLEAN ").append(entry.key).append(entry.getLengths()).append('\n').toString());
                    if (flag) {
                        l = nextSequenceNumber;
                        nextSequenceNumber = 1L + l;
                        entry.sequenceNumber = l;
                    }
                }
                journalWriter.flush();
                if (size > maxSize || journalRebuildRequired())
                    executorService.submit(cleanupCallable);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void deleteIfExists(File file)
    {
        if(file.exists())
        {
            if(!file.delete()) {
                try {
                    throw new IOException();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Editor edit(String s, long l)
    {
        Entry entry2;
        Entry entry;
        Entry entry1;
        Editor editor1 = null;
        try {
            synchronized (this) {
                checkNotClosed();
                validateKey(s);
                entry = (Entry) lruEntries.get(s);
                if (l != -1L) {
                    if (entry == null)
                        return null;
                    long l1 = entry.sequenceNumber;
                    if (l1 != l)
                        return null;
                }
                if (entry != null) {
                    Editor editor = entry.currentEditor;
                    if (editor == null) {
                        entry1 = entry;
                        editor1 = new Editor(entry1, null);
                        entry1.currentEditor = editor1;
                        journalWriter.write((new StringBuilder()).append("DIRTY ").append(s).append('\n').toString());
                        journalWriter.flush();
                    } else
                        editor1 = null;
                } else {
                    entry2 = new Entry(s, null);
                    lruEntries.put(s, entry2);
                    entry1 = entry2;
                    editor1 = new Editor(entry1, null);
                    entry1.currentEditor = editor1;
                    journalWriter.write((new StringBuilder()).append("DIRTY ").append(s).append('\n').toString());
                    journalWriter.flush();
                }
                return editor1;
            }
        }catch(Exception exception){
            try {
                throw exception;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static String inputStreamToString(InputStream inputstream)
    {
        return Util.readFully(new InputStreamReader(inputstream, Util.UTF_8));
    }

    private boolean journalRebuildRequired()
    {
        return redundantOpCount >= 2000 && redundantOpCount >= lruEntries.size();
    }

    public static DiskLruCache open(File file, int i, int j, long l)
    {
        DiskLruCache disklrucache;
        if(l <= 0L)
            throw new IllegalArgumentException("maxSize <= 0");
        if(j <= 0)
            throw new IllegalArgumentException("valueCount <= 0");
        File file1 = new File(file, "journal.bkp");
        if (file1.exists()) {
            File file2 = new File(file, "journal");
            if (file2.exists())
                file1.delete();
            else
                renameTo(file1, file2, false);
        }
        disklrucache = new DiskLruCache(file, i, j, l);
        try {
            if (!disklrucache.journalFile.exists()) {
                file.mkdirs();
                DiskLruCache disklrucache1 = new DiskLruCache(file, i, j, l);
                disklrucache1.rebuildJournal();
                return disklrucache1;
            }
            disklrucache.readJournal();
            disklrucache.processJournal();
            disklrucache.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(disklrucache.journalFile, true), Util.US_ASCII));
            return disklrucache;
        }catch(IOException ioexception){
            System.out.println((new StringBuilder()).append("DiskLruCache ").append(file).append(" is corrupt: ").append(ioexception.getMessage()).append(", removing").toString());
            disklrucache.delete();
            file.mkdirs();
            DiskLruCache disklrucache1 = new DiskLruCache(file, i, j, l);
            disklrucache1.rebuildJournal();
            return disklrucache1;
        }
    }

    private void processJournal()
    {
        deleteIfExists(journalFileTmp);
        for(Iterator iterator = lruEntries.values().iterator(); iterator.hasNext();)
        {
            Entry entry = (Entry)iterator.next();
            if(entry.currentEditor == null)
            {
                int j = 0;
                while(j < valueCount)
                {
                    size = size + entry.lengths[j];
                    j++;
                }
            } else
            {
                entry.currentEditor = null;
                for(int i = 0; i < valueCount; i++)
                {
                    deleteIfExists(entry.getCleanFile(i));
                    deleteIfExists(entry.getDirtyFile(i));
                }

                iterator.remove();
            }
        }

    }

    private void readJournal()
    {
        try {
            StrictLineReader strictlinereader = new StrictLineReader(new FileInputStream(journalFile), Util.US_ASCII);
            int i = 0;
            try {
                String s = strictlinereader.readLine();
                String s1 = strictlinereader.readLine();
                String s2 = strictlinereader.readLine();
                String s3 = strictlinereader.readLine();
                String s4 = strictlinereader.readLine();
                if (!"libcore.io.DiskLruCache".equals(s) || !"1".equals(s1) || !Integer.toString(appVersion).equals(s2) || !Integer.toString(valueCount).equals(s3) || !"".equals(s4)) {
                    try {
                        throw new IOException((new StringBuilder()).append("unexpected journal header: [").append(s).append(", ").append(s1).append(", ").append(s3).append(", ").append(s4).append("]").toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                while (true) {
                    readJournalLine(strictlinereader.readLine());
                    i++;
                }
            } catch (EOFException eofexception) {
                redundantOpCount = i - lruEntries.size();
                Util.closeQuietly(strictlinereader);
            }
        }catch(FileNotFoundException e1){
            e1.printStackTrace();
        }
    }

    private void readJournalLine(String s)
    {
        int i;
        int j;
        int k;
        Entry entry;
        String s1;
        String s2;
        i = s.indexOf(' ');
        try {
            if (i == -1) {
                throw new IOException((new StringBuilder()).append("unexpected journal line: ").append(s).toString());
            }
            j = i + 1;
            k = s.indexOf(' ', j);
            if (k != -1) {
                s1 = s.substring(j, k);
            } else {
                s2 = s.substring(j);
                if (i == "REMOVE".length() && s.startsWith("REMOVE")) {
                    lruEntries.remove(s2);
                    return;
                }
                s1 = s2;
            }
            entry = (Entry) lruEntries.get(s1);
            if (entry == null) {
                entry = new Entry(s1, null);
                lruEntries.put(s1, entry);
            }
            if (k != -1 && i == "CLEAN".length() && s.startsWith("CLEAN")) {
                String as[] = s.substring(k + 1).split(" ");
                entry.readable = true;
                entry.currentEditor = null;
                entry.setLengths(as);
                return;
            }
            if (k == -1 && i == "DIRTY".length() && s.startsWith("DIRTY")) {
                entry.currentEditor = new Editor(entry, null);
                return;
            }
            if (k == -1 && i == "READ".length() && s.startsWith("READ"))
                return;
            throw new IOException((new StringBuilder()).append("unexpected journal line: ").append(s).toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void rebuildJournal()
    {
        BufferedWriter bufferedwriter;
        Entry entry;
        try {
            synchronized (this) {
                if (journalWriter != null)
                    journalWriter.close();
                bufferedwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalFileTmp), Util.US_ASCII));
                Iterator iterator;
                bufferedwriter.write("libcore.io.DiskLruCache");
                bufferedwriter.write("\n");
                bufferedwriter.write("1");
                bufferedwriter.write("\n");
                bufferedwriter.write(Integer.toString(appVersion));
                bufferedwriter.write("\n");
                bufferedwriter.write(Integer.toString(valueCount));
                bufferedwriter.write("\n");
                bufferedwriter.write("\n");
                iterator = lruEntries.values().iterator();
                while (iterator.hasNext()) {
                    entry = (Entry) iterator.next();
                    if (entry.currentEditor == null)
                        bufferedwriter.write((new StringBuilder()).append("CLEAN ").append(entry.key).append(entry.getLengths()).append('\n').toString());
                    else
                        bufferedwriter.write((new StringBuilder()).append("DIRTY ").append(entry.key).append('\n').toString());
                }
                bufferedwriter.close();
                if (journalFile.exists()) {
                    renameTo(journalFile, journalFileBackup, true);
                }
                renameTo(journalFileTmp, journalFile, false);
                journalFileBackup.delete();
                journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalFile, true), Util.US_ASCII));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void renameTo(File file, File file1, boolean flag)
    {
        try {
            if (flag)
                deleteIfExists(file1);
            if (!file.renameTo(file1))
                throw new IOException();
        }catch(Exception e){}
    }

    private void trimToSize()
    {
        while(size > maxSize)
        {
            remove((String)((java.util.Map.Entry)lruEntries.entrySet().iterator().next()).getKey());
        }
    }

    private void validateKey(String s)
    {
        if(!LEGAL_KEY_PATTERN.matcher(s).matches())
        {
            throw new IllegalArgumentException((new StringBuilder()).append("keys must match regex [a-z0-9_-]{1,64}: \"").append(s).append("\"").toString());
        }
    }

    public void close()
    {
        try {
            synchronized (this) {
                Writer writer = journalWriter;
                if (writer != null) {
                    Iterator iterator = (new ArrayList(lruEntries.values())).iterator();
                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();
                        if (entry.currentEditor != null)
                            entry.currentEditor.abort();
                    }
                    trimToSize();
                    journalWriter.close();
                    journalWriter = null;
                }
            }
        }catch(Exception e){}
    }

    public void delete()
    {
        close();
        Util.deleteContents(directory);
    }

    public Editor edit(String s)
    {
        return edit(s, -1L);
    }

    public void flush()
    {
        try {
            synchronized (this) {
                checkNotClosed();
                trimToSize();
                journalWriter.flush();
            }
        }catch(Exception e){}
    }

    public Snapshot get(String s)
    {
        synchronized (this) {
            Entry entry;
            checkNotClosed();
            validateKey(s);
            entry = (Entry)lruEntries.get(s);
            Snapshot snapshot = null;
            if(entry == null)
                return snapshot;
            boolean flag = entry.readable;
            snapshot = null;
            try {
                if (!flag)
                    return snapshot;
                InputStream ainputstream[] = new InputStream[valueCount];
                try {
                    int i = 0;
                    while (i < valueCount) {
                        ainputstream[i] = new FileInputStream(entry.getCleanFile(i));
                        i++;
                    }
                    redundantOpCount = 1 + redundantOpCount;
                    journalWriter.append((new StringBuilder()).append("READ ").append(s).append('\n').toString());
                    if (journalRebuildRequired()) {
                        executorService.submit(cleanupCallable);
                    }
                    snapshot = new Snapshot(s, entry.sequenceNumber, ainputstream, entry.lengths, null);
                } catch (FileNotFoundException filenotfoundexception) {
                    int j = 0;
                    int k = valueCount;
                    snapshot = null;
                    while (j < k) {
                        InputStream inputstream = ainputstream[j];
                        snapshot = null;
                        if (inputstream == null)
                            break;
                        Util.closeQuietly(ainputstream[j]);
                        j++;
                    }
                }
                return snapshot;
            }catch(Exception e){
                return snapshot;
            }
        }
    }

    public File getDirectory()
    {
        return directory;
    }

    public int getItemsCount()
    {
        return lruEntries.size();
    }

    public long getMaxSize()
    {
        synchronized (this) {
            long l = maxSize;
            return l;
        }
    }

    public boolean isClosed()
    {
        synchronized (this) {
            Writer writer = journalWriter;
            boolean flag;
            if(writer == null)
                flag = true;
            else
                flag = false;
            return flag;
        }
    }

    public boolean remove(String s)
    {
        try {
            synchronized (this) {
                Entry entry;
                checkNotClosed();
                validateKey(s);
                entry = (Entry) lruEntries.get(s);
                if (entry == null)
                    return false;
                Editor editor = entry.currentEditor;
                int i = 0;
                if (editor != null)
                    return false;
                File file;
                while (i < valueCount) {
                    file = entry.getCleanFile(i);
                    if (file.exists()) {
                        if (!file.delete())
                            throw new IOException((new StringBuilder()).append("failed to delete ").append(file).toString());
                    }
                    size = size - entry.lengths[i];
                    entry.lengths[i] = 0L;
                    i++;
                }
                redundantOpCount = 1 + redundantOpCount;
                journalWriter.append((new StringBuilder()).append("REMOVE ").append(s).append('\n').toString());
                lruEntries.remove(s);
                if (journalRebuildRequired()) {
                    executorService.submit(cleanupCallable);
                }
                return true;
            }
        }catch(Exception e){
            return false;
        }
    }

    public void setMaxSize(long l)
    {
        synchronized (this) {
            maxSize = l;
            executorService.submit(cleanupCallable);
        }
    }

    public long size()
    {
        synchronized (this) {
            long l = size;
            return l;
        }
    }

    private class _cls1 implements Callable<Void>
    {
        @Override
        public Void call()
        {
            synchronized(DiskLruCache.this)
            {
                if(journalWriter != null)
                {
                    trimToSize();
                    if(journalRebuildRequired())
                    {
                        rebuildJournal();
                        redundantOpCount = 0;
                    }
                }
            }
            return null;
        }
    }


    private class Entry
    {
        private Editor currentEditor;
        private final String key;
        private final long lengths[];
        private boolean readable;
        private long sequenceNumber;

        private IOException invalidLengths(String as[])
        {
            try {
                throw new IOException((new StringBuilder()).append("unexpected journal line: ").append(Arrays.toString(as)).toString());
            } catch (IOException e) {
                return e;
            }
        }

        private void setLengths(String as[])
        {
            try {
                if (as.length != valueCount) {
                    throw invalidLengths(as);
                }
                int i = 0;
                try {
                    while (i < as.length) {
                        lengths[i] = Long.parseLong(as[i]);
                        i++;
                    }
                } catch (NumberFormatException numberformatexception) {
                    throw invalidLengths(as);
                }
            }catch(Exception e){}
        }

        public File getCleanFile(int i)
        {
            return new File(directory, (new StringBuilder()).append(key).append(".").append(i).toString());
        }

        public File getDirtyFile(int i)
        {
            return new File(directory, (new StringBuilder()).append(key).append(".").append(i).append(".tmp").toString());
        }

        public String getLengths()
        {
            StringBuilder stringbuilder = new StringBuilder();
            long al[] = lengths;
            int i = al.length;
            for(int j = 0; j < i; j++)
            {
                long l = al[j];
                stringbuilder.append(' ').append(l);
            }

            return stringbuilder.toString();
        }

        private Entry(String s)
        {
            super();
            key = s;
            lengths = new long[valueCount];
        }

        Entry(String s, _cls1 _pcls1)
        {
            this(s);
        }
    }

    public class Editor
    {
        private boolean committed;
        private final Entry entry;
        private boolean hasErrors;
        private final boolean written[];

        class FaultHidingOutputStream extends FilterOutputStream
        {
            public void close()
            {
                try {
                    out.close();
                } catch(IOException ioexception) {
                    hasErrors = true;
                }
            }

            public void flush()
            {
                try {
                    out.flush();
                } catch(IOException ioexception) {
                    hasErrors = true;
                }
            }

            public void write(int j)
            {
                try {
                    out.write(j);
                } catch(IOException ioexception) {
                    hasErrors = true;
                }
            }

            public void write(byte abyte0[], int j, int k)
            {
                try {
                    out.write(abyte0, j, k);
                } catch(IOException ioexception) {
                    hasErrors = true;
                }
            }

            private FaultHidingOutputStream(OutputStream outputstream)
            {
                super(outputstream);
            }

            FaultHidingOutputStream(OutputStream outputstream, _cls1 _pcls1)
            {
                this(outputstream);
            }
        }

        public void abort()
        {
            completeEdit(this, false);
        }

        public void abortUnlessCommitted()
        {
            if(!committed)
                abort();
        }

        public void commit()
        {
            if(hasErrors)
            {
                completeEdit(this, false);
                remove(entry.key);
            } else
            {
                completeEdit(this, true);
            }
            committed = true;
        }

        public String getString(int i)
        {
            InputStream inputstream = newInputStream(i);
            if(inputstream != null)
            {
                return DiskLruCache.inputStreamToString(inputstream);
            } else
            {
                return null;
            }
        }

        public InputStream newInputStream(int i)
        {
            DiskLruCache disklrucache = DiskLruCache.this;
            try {
                synchronized (disklrucache) {
                    if (entry.currentEditor != this) {
                        throw new IllegalStateException();
                    }
                    if (entry.readable) {
                        FileInputStream fileinputstream = new FileInputStream(entry.getCleanFile(i));
                        return fileinputstream;
                    }
                }
            }catch(Exception e){}
            return null;
        }

        public OutputStream newOutputStream(int i) //needdebug
        {
            DiskLruCache disklrucache = DiskLruCache.this;
            synchronized (disklrucache) {
                if(entry.currentEditor != this)
                {
                    throw new IllegalStateException();
                }
                File file;
                if(!entry.readable)
                {
                    written[i] = true;
                }
                file = entry.getDirtyFile(i);
                FileOutputStream fileoutputstream1;
                try {
                    FileOutputStream fileoutputstream = new FileOutputStream(file);
                    fileoutputstream1 = fileoutputstream;
                }catch(FileNotFoundException e){
                    directory.mkdirs();
                    try {
                        FileOutputStream fileoutputstream2 = new FileOutputStream(file);
                        fileoutputstream1 = fileoutputstream2;
                    }catch(FileNotFoundException filenotfoundexception1){
                        OutputStream outputstream = DiskLruCache.NULL_OUTPUT_STREAM;
                        return outputstream;
                    }
                }
                FaultHidingOutputStream faulthidingoutputstream = new FaultHidingOutputStream(fileoutputstream1, null);
                return faulthidingoutputstream;
            }
        }

        public void set(int i, String s)
        {
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(newOutputStream(i), Util.UTF_8);
            try {
                outputstreamwriter.write(s);
                Util.closeQuietly(outputstreamwriter);
            }catch(Exception e){
                Util.closeQuietly(outputstreamwriter);
                outputstreamwriter = null;
            }
        }

        private Editor(Entry entry1)
        {
            super();
            entry = entry1;
            boolean aflag[];
            if(entry1.readable)
            {
                aflag = null;
            } else
            {
                aflag = new boolean[valueCount];
            }
            written = aflag;
        }

        Editor(Entry entry1, _cls1 _pcls1)
        {
            this(entry1);
        }
    }


    public class Snapshot implements Closeable
    {
        private final InputStream ins[];
        private final String key;
        private final long lengths[];
        private final long sequenceNumber;

        public void close()
        {
            InputStream ainputstream[] = ins;
            int i = ainputstream.length;
            for(int j = 0; j < i; j++)
            {
                Util.closeQuietly(ainputstream[j]);
            }

        }

        public Editor edit()
        {
            return DiskLruCache.this.edit(key, sequenceNumber);
        }

        public InputStream getInputStream(int i)
        {
            return ins[i];
        }

        public long getLength(int i)
        {
            return lengths[i];
        }

        public String getString(int i)
        {
            return DiskLruCache.inputStreamToString(getInputStream(i));
        }

        private Snapshot(String s, long l, InputStream ainputstream[], long al[])
        {
            super();
            key = s;
            sequenceNumber = l;
            ins = ainputstream;
            lengths = al;
        }

        Snapshot(String s, long l, InputStream ainputstream[], long al[], _cls1 _pcls1)
        {
            this(s, l, ainputstream, al);
        }
    }

}
