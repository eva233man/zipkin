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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangjinpeng on 2018/4/15.
 */

public class SpanTagParserTest {
  private final SpanTagParser spanTagParser = new CustomerSpanTagParser();

  @Test
  public void testParser(){
    Map<String, String> tags = new HashMap<>();
    tags.put("component", "dubbo");
    tags.put("args", "A1060001,000993<?xml version=\"1.0\" encoding=\"utf-8\" ?><root><head><tranCode>A1060001</tranCode><serno>20180415084843154283</serno><tranDate>2018-04-15</tranDate><tranTime>08:48:43</tranTime><chlGrp>0016</chlGrp><bchCde>900000</bchCde><starecord></starecord><page></page><version>1.0</version></head><body><sysid>A101</sysid><custtyp>05</custtyp><lmttyp>10</lmttyp><lmtnature>02</lmtnature><cycind>Y</cycind><currency>CNY</currency><termtyp>M</termtyp><limit/><gurttyp/><lmtocctyp/><comments/><lmtsts/><mainusr/><mainbch/><crtusr/><crtbch/><list><itemlist><applseq>CT4287476151750684672</applseq><prdtyp>20</prdtyp><prdcde>5103</prdcde><prdname>晋情借</prdname><cycind>Y</cycind><currency>CNY</currency><total>200000.00</total><termtyp>M</termtyp><limit>99</limit><comments/><itemsts/></itemlist></list><custid>1000090207</custid><idtyp>20</idtyp><idno>500236198410162750</idno><custname>石宗星</custname><startdt>2018-04-15</startdt><totalamt>200000.00</totalamt><enddt>2999-01-01</enddt></body></root>");
    tags.put("result", "<root><head><tradeCode>A1060001</tradeCode><successCode>E00000000000</successCode><errorMessage>交易成功</errorMessage></head><body/></root>");

    spanTagParser.parse(tags);

//    System.out.println(JSON.toJSONString(tags));
  }

}
