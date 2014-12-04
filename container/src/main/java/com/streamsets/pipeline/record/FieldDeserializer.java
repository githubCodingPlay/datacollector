/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.record;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.streamsets.pipeline.api.Field;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldDeserializer extends JsonDeserializer<Field> {

  @Override
  @SuppressWarnings("unchecked")
  public Field deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    return parse(jp.readValueAs(Map.class));
  }

  @SuppressWarnings("unchecked")
  private Field parse(Map<String, Object> map) {
    Field field = null;
    if (map != null) {
      Field.Type type = Field.Type.valueOf((String) map.get("type"));
      Object value = map.get("value");
      if (value != null) {
        switch (type) {
          case MAP:
            Map<String, Field> fMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
              fMap.put(entry.getKey(), parse((Map<String, Object>) entry.getValue()));
            }
            value = fMap;
            break;
          case LIST:
            List<Field> fList = new ArrayList<>();
            for (Map<String, Object> element : (List<Map<String, Object>>) value) {
              fList.add(parse(element));
            }
            value = fList;
            break;
          default:
            break;
        }
      }
      field = Field.create(type, value);
    }
    return field;
  }

}
