package org.finos.symphony.toolkit.workflow.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = HashTagDef.class)
public interface HashTag extends Tag {

}
