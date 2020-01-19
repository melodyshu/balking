package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Document {
    private boolean changed=false;

    private List<String> content=new ArrayList<>();

    private final FileWriter fileWriter;

    private static AutoSaveThread autoSaveThread;

    private Document(String documentPath,String documentName) throws IOException {
        this.fileWriter=new FileWriter(new File(documentPath,documentName));
    }

    public static Document create(String documentPath,String documentName) throws IOException {
        Document document=new Document(documentPath,documentName);
        autoSaveThread=new AutoSaveThread(document);
        autoSaveThread.start();
        return document;
    }
    public void edit(String content){
        synchronized (this){
            this.content.add(content);
            this.changed=true;
        }
    }

    public void close() throws IOException {
        autoSaveThread.interrupt();
        fileWriter.close();
    }

    public void save() throws IOException {
        synchronized (this){
            if(!changed){
                return;
            }
            System.out.println(Thread.currentThread()+" execute the save action");
            for (String line : content) {
                this.fileWriter.write(line);
                this.fileWriter.write("\r\n");
            }
            this.fileWriter.flush();
            this.changed=false;
            this.content.clear();
        }
    }
}
