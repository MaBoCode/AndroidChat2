package com.example.app_template_java.utils;

import android.util.Log;

public class Logs {

    public static String tag = "[MDB_LOG]";

    public enum Type {
        VERBOSE("VERBOSE"),
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARN("WARN"),
        ERROR("ERROR");

        private String type;

        Type(String type) {
            this.type = type;
        }
    }

    public static class Builder {
        private String tag;
        private String message;
        private String method;
        private String object;
        private Type type;

        public Builder(String message) {
            this.message = message;
        }

        protected Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        protected Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder method() {
            this.method = Thread.currentThread().getStackTrace()[4].getMethodName();

            if (method.equals("verbose") || method.equals("debug")
                    || method.equals("info") || method.equals("method")
                    || method.equals("warn") || method.equals("error")) {
                this.method = Thread.currentThread().getStackTrace()[5].getMethodName();
            }

            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder object(Object object) {
            this.object = object.getClass().getName();
            return this;
        }

        public void print() {
            StringBuilder log = new StringBuilder();

            if (object != null && !object.isEmpty()) {
                log.append("[" + object + "]");
            }

            if (method != null && !method.isEmpty()) {
                log.append(method);
                if (message == null || message.isEmpty()) {
                    log.append("()");
                } else {
                    log.append(" ");
                }
            }

            if (message != null && !message.isEmpty()) {
                if (method != null && !method.isEmpty()) {
                    log.append("(" + message + ")");
                } else {
                    log.append(message);
                }
            }

            switch (type) {
                case DEBUG:
                    Log.d(tag, log.toString());
                    break;
                case ERROR:
                    Log.e(tag, log.toString());
                    break;
                case INFO:
                    Log.i(tag, log.toString());
                    break;
                case VERBOSE:
                    Log.v(tag, log.toString());
                    break;
                case WARN:
                    Log.w(tag, log.toString());
                    break;
            }
        }
    }

    public static void method(Object object, String message) {
        new Builder(message).type(Type.VERBOSE).tag(tag).object(object).method().print();
    }

    public static void method(Object object) {
        method(object, null);
    }

    public static void verbose(Object object, String message) {
        new Builder(message).type(Type.VERBOSE).tag(tag).object(object).method().print();
    }

    public static void debug(Object object, String message) {
        new Builder(message).type(Type.DEBUG).tag(tag).object(object).method().print();
    }

    public static void info(Object object, String message) {
        new Builder(message).type(Type.INFO).tag(tag).object(object).method().print();
    }

    public static void warn(Object object, String message) {
        new Builder(message).type(Type.WARN).tag(tag).object(object).method().print();
    }

    public static void error(Object object, String message) {
        new Builder(message).type(Type.ERROR).tag(tag).object(object).method().print();
    }
}