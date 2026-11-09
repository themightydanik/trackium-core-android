package com.eurobuddha.minimacore.shell;
import org.junit.Test;
import org.minima.utils.json.JSONObject;
import static org.junit.Assert.*;
public class NodeObservationTest {
    @Test public void disconnectedAndFailedReadsNeverProduceObservedChainData(){
        JSONObject reply=new JSONObject();reply.put("status",false);
        NodeObservation n=NodeObservation.parse(true,reply,123,0);
        assertFalse(n.observed);assertNull(n.block);assertEquals(0,n.at);
        assertFalse(NodeObservation.parse(false,null,123,0).running);
    }
    @Test public void peersAndBlockAreIndependentEvidenceAndMissingFieldsStayUnknown(){
        JSONObject network=new JSONObject();network.put("connected",2L);
        JSONObject chain=new JSONObject();chain.put("block","1234");
        JSONObject response=new JSONObject();response.put("network",network);response.put("chain",chain);
        JSONObject reply=new JSONObject();reply.put("status",true);reply.put("response",response);
        NodeObservation n=NodeObservation.parse(true,reply,123,1);
        assertTrue(n.observed);assertEquals("2",n.peers);assertEquals("1234",n.block);assertEquals(123,n.at);
        response.remove("chain");assertNull(NodeObservation.parse(true,reply,124,1).block);
    }
}
