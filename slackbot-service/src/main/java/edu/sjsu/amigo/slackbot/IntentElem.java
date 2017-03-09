package edu.sjsu.amigo.slackbot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The intent object returned from the wit.ai service.
 *
 * {
         "msg_id" : "5e89f3d9-93d1-4dfa-b128-73fda21974b3",
         "_text" : "list my ec2 instances on aws and azure",
         "entities" : {
             "intent" : [ {
                 "confidence" : 0.9771069792658154,
                 "type" : "value",
                 "value" : "list"
                 }, {
                 "confidence" : 0.9721842327959846,
                 "type" : "value",
                 "value" : "ec2"
                 }, {
                 "confidence" : 0.873736763412216,
                 "type" : "value",
                 "value" : "aws"
                 }, {
                 "confidence" : 0.7501236802989762,
                 "type" : "value",
                 "value" : "azure",
                 "suggested" : true
             } ]
         }
 }
 This class represents each element of intent JSON array.
 *
 * @author rwatsh on 3/8/17.
 */
@Data
public class IntentElem {
    @JsonProperty
    private String confidence;
    @JsonProperty
    private String type;
    @JsonProperty
    private String value;
    @JsonProperty
    private boolean suggested;
}
