package com.wechat.bot.util;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;

/**
 * @author Alex
 * @since 2025/4/1 16:20
 * <p>
 * 微信消息解析器，将xml数据解析成正确的数据对象
 * </p>
 */
public class WechatMsgParser {

    public static void main(String[] args) throws DocumentException {

        String xmlStr = "<?xml version=\"1.0\"?>\n<msg>\n\t<appmsg appid=\"\" sdkver=\"0\">\n\t\t<title>看看这个</title>\n\t\t<des />\n\t\t<action />\n\t\t<type>57</type>\n\t\t<showtype>0</showtype>\n\t\t<soundtype>0</soundtype>\n\t\t<mediatagname />\n\t\t<messageext />\n\t\t<messageaction />\n\t\t<content />\n\t\t<contentattr>0</contentattr>\n\t\t<url />\n\t\t<lowurl />\n\t\t<dataurl />\n\t\t<lowdataurl />\n\t\t<appattach>\n\t\t\t<totallen>0</totallen>\n\t\t\t<attachid />\n\t\t\t<emoticonmd5 />\n\t\t\t<fileext />\n\t\t\t<aeskey />\n\t\t</appattach>\n\t\t<extinfo />\n\t\t<sourceusername />\n\t\t<sourcedisplayname />\n\t\t<thumburl />\n\t\t<md5 />\n\t\t<statextstr />\n\t\t<refermsg>\n\t\t\t<type>49</type>\n\t\t\t<svrid>3617029648443513152</svrid>\n\t\t\t<fromusr>wxid_phyyedw9xap22</fromusr>\n\t\t\t<chatusr>wxid_phyyedw9xap22</chatusr>\n\t\t\t<displayname>朝夕。</displayname>\n\t\t\t<content>&lt;msg&gt;&lt;appmsg appid=\"\"  sdkver=\"0\"&gt;&lt;title&gt;hhh.xlsx&lt;/title&gt;&lt;des&gt;&lt;/des&gt;&lt;action&gt;&lt;/action&gt;&lt;type&gt;6&lt;/type&gt;&lt;showtype&gt;0&lt;/showtype&gt;&lt;soundtype&gt;0&lt;/soundtype&gt;&lt;mediatagname&gt;&lt;/mediatagname&gt;&lt;messageext&gt;&lt;/messageext&gt;&lt;messageaction&gt;&lt;/messageaction&gt;&lt;content&gt;&lt;/content&gt;&lt;contentattr&gt;0&lt;/contentattr&gt;&lt;url&gt;&lt;/url&gt;&lt;lowurl&gt;&lt;/lowurl&gt;&lt;dataurl&gt;&lt;/dataurl&gt;&lt;lowdataurl&gt;&lt;/lowdataurl&gt;&lt;appattach&gt;&lt;totallen&gt;8939&lt;/totallen&gt;&lt;attachid&gt;@cdn_3057020100044b304902010002043904752002032f7e350204aa0dd83a020465a0e897042430373538386564322d353866642d343234342d386563652d6236353536306438623936610204011800050201000405004c56f900_3f28b0cbd65a86c3a980f3e22808c0fe_1&lt;/attachid&gt;&lt;emoticonmd5&gt;&lt;/emoticonmd5&gt;&lt;fileext&gt;xlsx&lt;/fileext&gt;&lt;cdnattachurl&gt;3057020100044b304902010002043904752002032f7e350204aa0dd83a020465a0e897042430373538386564322d353866642d343234342d386563652d6236353536306438623936610204011800050201000405004c56f900&lt;/cdnattachurl&gt;&lt;aeskey&gt;3f28b0cbd65a86c3a980f3e22808c0fe&lt;/aeskey&gt;&lt;encryver&gt;0&lt;/encryver&gt;&lt;overwrite_newmsgid&gt;1789783684714859663&lt;/overwrite_newmsgid&gt;&lt;fileuploadtoken&gt;v1_paVQtd+CWGr2I3eOg71E6KBpQf0yY9RFQkqDPwT4yMnnbawqveao1vAE0qCOhWcIPkMGZavimUTDFcImr+SaManD8pKVQbBPTUvSmA6UsXgZWqQDOT00VLx7U/hoP3/CwveN2Lk56nxcef/XJiGKrOpAHKHcZvccaGk9/68wsBCOyanya/9xgdHTYxyQp4IadiSe&lt;/fileuploadtoken&gt;&lt;/appattach&gt;&lt;extinfo&gt;&lt;/extinfo&gt;&lt;sourceusername&gt;&lt;/sourceusername&gt;&lt;sourcedisplayname&gt;&lt;/sourcedisplayname&gt;&lt;thumburl&gt;&lt;/thumburl&gt;&lt;md5&gt;84c6737fe9549270c9b3ca4f6fc88f6f&lt;/md5&gt;&lt;statextstr&gt;&lt;/statextstr&gt;&lt;/appmsg&gt;&lt;fromusername&gt;&lt;/fromusername&gt;&lt;appinfo&gt;&lt;version&gt;0&lt;/version&gt;&lt;appname&gt;&lt;/appname&gt;&lt;isforceupdate&gt;1&lt;/isforceupdate&gt;&lt;/appinfo&gt;&lt;/msg&gt;</content>\n\t\t\t<msgsource>&lt;msgsource&gt;\n\t&lt;alnode&gt;\n\t\t&lt;cf&gt;3&lt;/cf&gt;\n\t&lt;/alnode&gt;\n\t&lt;sec_msg_node&gt;\n\t\t&lt;uuid&gt;896374a2b5979141804d509256c22f0b_&lt;/uuid&gt;\n\t&lt;/sec_msg_node&gt;\n&lt;/msgsource&gt;\n</msgsource>\n\t\t</refermsg>\n\t</appmsg>\n\t<fromusername>wxid_phyyedw9xap22</fromusername>\n\t<scene>0</scene>\n\t<appinfo>\n\t\t<version>1</version>\n\t\t<appname></appname>\n\t</appinfo>\n\t<commenturl></commenturl>\n</msg>\n"; // 用户提供的完整XML

        // 解析外层消息结构
        MsgInfo msgInfo = parseXml(xmlStr);
        System.out.println(JSONObject.from(msgInfo));
    }

    public static MsgInfo parseXml(String content) {

        MsgInfo msgInfo = null;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new ByteArrayInputStream(content.getBytes()));
            Element root = document.getRootElement();

            msgInfo = new MsgInfo();

            // 解析外层appmsg节点[1,5](@ref)
            Element appMsgElement = root.element("appmsg");
            if (appMsgElement != null) {
                msgInfo.setTitle(appMsgElement.elementText("title"));
                msgInfo.setType(appMsgElement.elementText("type"));

                // 解析附件信息[5](@ref)
                Element appAttach = appMsgElement.element("appattach");
                if (appAttach != null) {
                    AppAttach attach = new AppAttach();
                    attach.setAttachId(appAttach.elementText("attachid"));
                    attach.setFileExt(appAttach.elementText("fileext"));
                    attach.setAesKey(appAttach.elementText("aeskey"));
                    msgInfo.setAppAttach(attach);
                }

                // 解析嵌套的refermsg[5](@ref)
                //Element referMsg = appMsgElement.element("refermsg");
                //if (referMsg != null) {
                //    ReferMsg refer = new ReferMsg();
                //    refer.setContent(unescapeXml(referMsg.elementText("content")));
                //    refer.setMsgSource(referMsg.elementText("msgsource"));
                //
                //    // 解析嵌套的content XML[3](@ref)
                //    if (refer.getContent() != null) {
                //        Document innerDoc = DocumentHelper.parseText(refer.getContent());
                //        Element innerRoot = innerDoc.getRootElement();
                //        Element innerAppMsg = innerRoot.element("appmsg");
                //
                //        if (innerAppMsg != null) {
                //            refer.setInnerTitle(innerAppMsg.elementText("title"));
                //            // 可以继续解析更多嵌套字段...
                //        }
                //    }
                //    msgInfo.setReferMsg(refer);
                //}
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return msgInfo;
    }

    // 处理XML转义字符[3](@ref)
    private static String unescapeXml(String str) {

        return str.replace("&lt;", "<").replace("&gt;", ">")
                .replace("&amp;", "&").replace("&apos;", "'")
                .replace("&quot;", "\"");
    }

    // 数据结构定义
    @Getter
    @Setter
    public static class MsgInfo {

        private String title;

        private String type;

        private AppAttach appAttach;

        private ReferMsg referMsg;
        // getters & setters...
    }

    @Getter
    @Setter
    public static class AppAttach {

        private String attachId;

        private String fileExt;

        private String aesKey;
        // getters & setters...
    }

    @Getter
    @Setter
    public static class ReferMsg {

        private String content;

        private String msgSource;

        private String innerTitle;
        // getters & setters...
    }

}
