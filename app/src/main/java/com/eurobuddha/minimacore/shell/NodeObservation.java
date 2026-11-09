package com.eurobuddha.minimacore.shell;

import org.minima.utils.json.JSONObject;
/** Display-only evidence. No inference of chain synchronization from peer count. */
public final class NodeObservation {
    public final boolean running, observed;
    public final String peers, block;
    public final long at;
    public final int gateway;
    public NodeObservation(boolean running, boolean observed, String peers, String block, long at, int gateway) {
        this.running=running;this.observed=observed;this.peers=peers;this.block=block;this.at=at;this.gateway=gateway;
    }
    public static NodeObservation parse(boolean running, JSONObject reply, long now, int gateway) {
        if(!running||reply==null||!Boolean.TRUE.equals(reply.get("status")))return new NodeObservation(running,false,null,null,0,gateway);
        Object response=reply.get("response");
        if(!(response instanceof JSONObject))return new NodeObservation(running,false,null,null,0,gateway);
        JSONObject r=(JSONObject)response;
        return new NodeObservation(running,true,value(r,"network","connected"),value(r,"chain","block"),now,gateway);
    }
    private static String value(JSONObject r,String group,String key){
        Object part=r.get(group);if(!(part instanceof JSONObject))return null;
        Object v=((JSONObject)part).get(key);return v==null?null:String.valueOf(v);
    }
}
