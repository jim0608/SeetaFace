package com.mysafe.lib_base.util.log;

import java.util.ArrayList;

public class ProcessInfo {
        private String user;
        private String pid;
        private String ppid;
        private String vsize;
        private String rss;
        private String wchan;
        private String pc;
        private String name;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPpid() {
        return ppid;
    }

    public void setPpid(String ppid) {
        this.ppid = ppid;
    }

    public String getVsize() {
        return vsize;
    }

    public void setVsize(String vsize) {
        this.vsize = vsize;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public String getWchan() {
        return wchan;
    }

    public void setWchan(String wchan) {
        this.wchan = wchan;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessInfo(String info) {
            if (info == null) {
                return;
            }
            ArrayList<String> item = new ArrayList<String>();
            for (int i = 0; i < info.length(); i++) {
                if (info.charAt(i) == ' ') {
                    continue;
                }
                int j = i + 1;
                while (j < info.length() && info.charAt(j++) != ' ') ;
                item.add(info.substring(i, j - 1));
                i = j - 1;
            }

            user = item.get(0);
            pid = item.get(1);
            ppid = item.get(2);
            vsize = item.get(3);
            rss = item.get(4);
            wchan = item.get(5);
            pc = item.get(6);
            name = item.get(8);
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append(user).
                    append(" ").
                    append(pid).
                    append(" ").
                    append(ppid).
                    append(" ").
                    append(vsize).
                    append(" ").
                    append(rss).
                    append(" ").
                    append(wchan).
                    append(" ").
                    append(pc).
                    append(" ").
                    append(name);

            return buffer.toString();
        }
    }
