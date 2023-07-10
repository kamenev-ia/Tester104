package org.openmuc.j60870.gui.utilities;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.io.*;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DocumentHandler {
    private final Configuration configuration;

    public DocumentHandler() {
        configuration = new Configuration(new Version("2.3.22"));
        configuration.setDefaultEncoding("utf-8");
    }

    public Template getTemplate() {
        Template t = null;
        configuration.setClassForTemplateLoading(DocumentHandler.class, "/");
        try {
            t = configuration.getTemplate("Протокол КИ.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    public Writer getWriter(String savePath) {
        File outFile = new File(savePath);
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), UTF_8));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return out;
    }

    public void createDoc(Template t, Map dataMap, Writer out) {
        try {
            t.process(dataMap, out);
            out.close();
        } catch (TemplateException | IOException templateException) {
            templateException.printStackTrace();
        }
    }
}
