/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.elasticsearch.internal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by zhangjinpeng on 2018/4/15.
 */

public class CustomerSpanTagParser implements SpanTagParser {

  private static final Pattern XML_PATTERN = Pattern.compile("^([0-9]{6})(<)$");
  private static final Pattern JSON_PATTERN = Pattern.compile("^([0-9]{6})(\\{)$");

  @Override
  public void parse(Map<String, String> tags) {
    if("dubbo".equals(tags.get("component"))) {
      //增加对span中tags下的args、result的解析
      if (tags.containsKey("args")) {
        int idx = tags.get("args").indexOf(",");
        if(idx>0 || idx<=15) {
          String args = tags.get("args").substring(idx+1);
          if (args.charAt(0) == '{') {//json格式
            parseTagsArgsByJson(tags, args);
          }
          else if(args.charAt(0) == '<'){
            parseTagsArgsByXml(tags, args);
          }
          else {
            String subArgs = args.substring(0,7);
            if(XML_PATTERN.matcher(subArgs).matches()){
              parseTagsArgsByXml(tags, args.substring(6));
            }
            else if(JSON_PATTERN.matcher(subArgs).matches()){
              parseTagsArgsByJson(tags, args.substring(6));
            }
          }
        }
      }
      if (tags.containsKey("result")) {
        String result = tags.get("result");
        if(result.charAt(0)=='<') {
          parseTagsResultByXml(tags, result);
        }
        else if(result.charAt(0)=='{'){
          parseTagsResultByJson(tags, result);
        }
      }
    }
  }
  private void parseTagsArgsByXml(Map<String, String> tags, String args) {
    Document doc = null;
    try {
      doc = DocumentHelper.parseText(args);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    if (doc != null) {
      Element root = doc.getRootElement();
      for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
        Element e = (Element) iterator.next();
        if("head".equals(e.getName())){
          for (Iterator headIt = e.elementIterator(); headIt.hasNext(); ) {
            Element node = (Element) headIt.next();
            tags.put("args." + node.getName(), node.getText());
          }
        }
      }
    }
  }

  private void parseTagsArgsByJson(Map<String, String> tags, String args) {
    JSONObject resultJson = JSON.parseObject(args);
    if(resultJson.containsKey("head")){
      JSONObject head = resultJson.getJSONObject("head");
      for (String key : head.keySet()) {
        tags.put("args." + key, head.getString(key));
      }
    }
  }

  private void parseTagsResultByJson(Map<String, String> tags, String result) {
    JSONObject resultJson = JSON.parseObject(result);
    if(resultJson.containsKey("head")){
      JSONObject head = resultJson.getJSONObject("head");
      for (String key : head.keySet()) {
        tags.put("result." + key, head.getString(key));
      }
    }
  }

  private void parseTagsResultByXml(Map<String, String> tags, String result) {
    Document doc = null;
    try {
      doc = DocumentHelper.parseText(result);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    if (doc != null) {
      Element root = doc.getRootElement();
      for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
        Element e = (Element) iterator.next();
        if("head".equals(e.getName())){
          for (Iterator headIt = e.elementIterator(); headIt.hasNext(); ) {
            Element node = (Element) headIt.next();
            tags.put("result." + node.getName(), node.getText());
          }
        }
      }
    }
  }

}
