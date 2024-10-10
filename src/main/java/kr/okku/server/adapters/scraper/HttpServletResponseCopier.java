package kr.okku.server.adapters.scraper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpServletResponseCopier extends HttpServletResponseWrapper {

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter printWriter;

    public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new ServletOutputStreamCopier(super.getOutputStream(), byteArrayOutputStream);
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (printWriter == null) {
            outputStream = getOutputStream();
            printWriter = new PrintWriter(outputStream, true);
        }
        return printWriter;
    }

    public byte[] getCopy() {
        return byteArrayOutputStream.toByteArray();
    }
}

class ServletOutputStreamCopier extends ServletOutputStream {

    private ServletOutputStream outputStream;
    private ByteArrayOutputStream byteArrayOutputStream;

    public ServletOutputStreamCopier(ServletOutputStream outputStream, ByteArrayOutputStream byteArrayOutputStream) {
        this.outputStream = outputStream;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        byteArrayOutputStream.write(b);
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener listener) {

    }
}
