package com.eurobuddha.minimacore.shell;

import android.os.Bundle;
/** Only allowlisted presentation values are saved. No commands, credentials or domain objects. */
public final class ShellState {
    public int root=0;
    public int tool=-1;
    public int scroll=0;
    public void restore(Bundle b) {
        if(b==null)return;
        int r=b.getInt("shell.root",0);root=(r==2||r==3)?r:0;
        int t=b.getInt("shell.tool",-1);tool=(root==3&&t>=0&&t<=3)?t:-1;
        scroll=Math.max(0,b.getInt("shell.scroll",0));
    }
    public void save(Bundle b) {b.putInt("shell.root",root);b.putInt("shell.tool",tool);b.putInt("shell.scroll",scroll);}
    public boolean back() {if(tool!=-1){tool=-1;root=3;return true;}if(root!=0){root=0;return true;}return false;}
}
