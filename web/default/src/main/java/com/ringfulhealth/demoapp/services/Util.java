package com.ringfulhealth.demoapp.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.petebevin.markdown.MarkdownProcessor;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.security.Key;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class Util {
    
    public static String prefix = "http://demoapp.ringfulhealth.com";
    public static String shorturl = "http://demoapp.ringfulhealth.com";
    public static String systemPhone = "+15623143278";
    public static String systemEmail = "no-reply@ringfulhealth.com";
    
    public static String bitlyApiKey = "TBD";
    public static String mandrillApiKey = "TBD";
    public static String awsApiName = "TBD";
    public static String awsApiKey = "TBD";
    public static String twilioApiName = "TBD";
    public static String twilioApiKey = "TBD";
    
    
    public static String randomNumericString (int dig) {
        StringBuffer buf = new StringBuffer ();
        Random generator = new Random();
        for (int i = 0; i < dig; i++) {
            buf.append (generator.nextInt(10));
        }
        return buf.toString ();
    }

    public static String getRandomId (int dig) {
        String charTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuffer buf = new StringBuffer ();
        Random rand = new Random ();
        for (int i = 0; i < dig; i++) {
            buf.append (charTable.charAt(rand.nextInt(61)));
        }
        return buf.toString ();
    }

    public static String transformBitly (String longurl) {
        String req = "http://api.bitly.com/v3/shorten?login=myuan&apiKey=" + bitlyApiKey  + "&longUrl=" + URLEncoder.encode(longurl) + "&domain=j.mp&format=json";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(req);
        try {
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                JSONObject json = new JSONObject (EntityUtils.toString(entity));
                if (json.getInt("status_code") != 200) {
                    return null;
                }
                JSONObject data = json.getJSONObject("data");
                return data.getString("url");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }

    public static void sendSms (String from, String to, String msg) {
        TwilioRestClient client = new TwilioRestClient (twilioApiName, twilioApiKey, null);
        String path = "/2010-04-01/Accounts/"+client.getAccountSid()+"/SMS/Messages";

        if (from == null || from.trim().isEmpty()) {
            System.out.println ("There is no FROM");
            return;
        }
        if (to == null || to.trim().isEmpty()) {
            System.out.println ("There is no TO");
            return;
        }
        if (msg == null || msg.trim().isEmpty()) {
            System.out.println ("There is no msg body");
            return;
        }

        Map<String, String> vars = new HashMap <String, String> ();
        vars.put("From", from);
        vars.put("To", to);
        vars.put("Body", msg);

        try {
            TwilioRestResponse tresp = client.request(path, "POST", vars);
            if (tresp.isError()) {
                System.out.println("Twilio SMS error: " + tresp.getResponseText());
            } else {
                System.out.println("Twilio SMS success: " + tresp.getResponseText());
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public static void makeCall (String from, String to, String prefix, String txml) {
        TwilioRestClient client = new TwilioRestClient (twilioApiName, twilioApiKey, null);
        String path = "/2010-04-01/Accounts/"+client.getAccountSid()+"/Calls";

        if (from == null || from.trim().isEmpty()) {
            System.out.println ("There is no FROM");
            return;
        }
        if (to == null || to.trim().isEmpty()) {
            System.out.println ("There is no TO");
            return;
        }

        Map<String, String> vars = new HashMap <String, String> ();
        vars.put("From", from);
        vars.put("To", to);
        vars.put("Url", prefix + "/" + txml);

        try {
            TwilioRestResponse tresp = client.request(path, "POST", vars);
            if (tresp.isError()) {
                System.out.println("Twilio Voice error: " + tresp.getResponseText());
            } else {
                System.out.println("Twilio Voice success: " + tresp.getResponseText());
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /*
    public static void sendEmail (String from, String to, String subject, String body) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsApiName, awsApiKey);
        // AmazonSimpleEmailServiceAsyncClient client = new AmazonSimpleEmailServiceAsyncClient(awsCredentials);
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCredentials);


        Message message = new Message ();
        message.setSubject(new Content(subject));
        message.setBody(new Body(new Content(body)));

        List <String> toAddresses = new ArrayList <String> ();
        toAddresses.add (to);
        Destination dest = new Destination (toAddresses);

        SendEmailRequest emailReq = new SendEmailRequest(from, dest, message);
        SendEmailResult result = client.sendEmail(emailReq);
        System.out.println("Email sending result is " + result.toString());
    }
    */
    
    public static void sendHtmlEmail (String from, String to, String subject, String template, Hashtable<String, String> params, String tag) {
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            template = template.replace("#{" + entry.getKey() + "}", entry.getValue());
        }
        
        MandrillApi mandrillApi = new MandrillApi(mandrillApiKey);

        MandrillMessage message = new MandrillMessage();
        message.setSubject(subject);
        message.setHtml(template);
        message.setAutoText(true);
        message.setFromEmail(from);
        message.setFromName("Ringful Health");
        // add recipients
        ArrayList<Recipient> recipients = new ArrayList<Recipient>();
        Recipient recipient = new Recipient();
        recipient.setEmail(to);
        // recipient.setName("Claire Annette");
        recipients.add(recipient);
        message.setTo(recipients);
        message.setPreserveRecipients(true);
        message.setTrackingDomain("vamoscolumbia.com");

        ArrayList<String> tags = new ArrayList<String>();
        tags.add(tag);
        message.setTags(tags);

        try {
            MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().send(message, false);
            for (MandrillMessageStatus s : messageStatusReports) {
                System.out.println("Email HTML sending result for " + s.getEmail() + " is " + s.getStatus());
            }
            
        } catch (MandrillApiError e) {
            e.printStackTrace();
            System.out.print(e.getMandrillErrorAsJson());
        } catch (Exception ee) {
            ee.printStackTrace ();
        }

    }

    public static String formatPhoneNumber (String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber number = phoneUtil.parse(phone, "US");
            if (!phoneUtil.isValidNumber(number)) {
                throw new Exception ("Phone number validation failed");
            }
            return phoneUtil.format(number, PhoneNumberFormat.E164);
        } catch (Exception e) {
            e.printStackTrace ();
            return "";
        }
    }
    
    public static String getElapsedTime(Date created) {
        long duration = System.currentTimeMillis() - created.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if (days > 0) {
            return days + " days ago";
        }
        if (hours > 0) {
            return hours + " hrs ago";
        }
        if (minutes > 0) {
            return minutes + " minutes ago";
        }

        return seconds + " seconds ago";
    }
    
    public static String formatDate (Date d) {
        if (d == null) {
            return "";
        } else {
            DateFormat df = new SimpleDateFormat ("MM/dd/yyyy");
            return df.format (d);
        }
    }
    
    public static String formatDateTime (Date d) {
        if (d == null) {
            return "";
        } else {
            DateFormat df = new SimpleDateFormat ("MM/dd/yyyy hh:mm");
            return df.format (d);
        }
    }
    
    public static String cleanUpFormData (String para) {
        if (para == null) {
            para = "";
        } else {
            para = para.trim();
        }
        return para;
    }
    
    
    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }

    
    public static String markdown (String para) {
        MarkdownProcessor markdown = new MarkdownProcessor();
        return markdown.markdown(para);
    }
    
    public static String createHtmlFields (Date date) {
        Calendar cal = null;
        if (date != null) {
            cal = Calendar.getInstance();
            cal.setTime(date);
        }
        
        StringBuilder sb = new StringBuilder ();
        
        sb.append("<select name=\"dobMonth\" class=\"input-small\">");
        sb.append("<option value=\"na\">Month</option>");
        if (cal != null && cal.get(Calendar.MONTH) == 0) {
            sb.append("<option value=\"1\" selected>January</option>");
        } else {
            sb.append("<option value=\"1\">January</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 1) {
            sb.append("<option value=\"2\" selected>February</option>");
        } else {
            sb.append("<option value=\"2\">February</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 2) {
            sb.append("<option value=\"3\" selected>March</option>");
        } else {
            sb.append("<option value=\"3\">March</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 3) {
            sb.append("<option value=\"4\" selected>April</option>");
        } else {
            sb.append("<option value=\"4\">April</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 4) {
            sb.append("<option value=\"5\" selected>May</option>");
        } else {
            sb.append("<option value=\"5\">May</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 5) {
            sb.append("<option value=\"6\" selected>June</option>");
        } else {
            sb.append("<option value=\"6\">June</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 6) {
            sb.append("<option value=\"7\" selected>July</option>");
        } else {
            sb.append("<option value=\"7\">July</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 7) {
            sb.append("<option value=\"8\" selected>August</option>");
        } else {
            sb.append("<option value=\"8\">August</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 8) {
            sb.append("<option value=\"9\" selected>September</option>");
        } else {
            sb.append("<option value=\"9\">September</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 9) {
            sb.append("<option value=\"10\" selected>October</option>");
        } else {
            sb.append("<option value=\"10\">October</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 10) {
            sb.append("<option value=\"11\" selected>November</option>");
        } else {
            sb.append("<option value=\"11\">November</option>");
        }
        if (cal != null && cal.get(Calendar.MONTH) == 11) {
            sb.append("<option value=\"12\" selected>December</option>");
        } else {
            sb.append("<option value=\"12\">December</option>");
        }
        sb.append("</select>");
        
        sb.append("<select name=\"dobDay\" class=\"input-small\">");
        sb.append("<option value=\"na\">Day</option>");
        for (int i = 1; i <= 31; i++) {
            sb.append("<option value=\"" + i + "\"");
            if (cal != null && cal.get(Calendar.DAY_OF_MONTH) == i) {
                sb.append(" selected>" + i + "</option>");
            } else {
                sb.append(">" + i + "</option>");
            }
        }
        sb.append("</select>");
        
        sb.append("<select name=\"dobYear\" class=\"input-small\">");
        sb.append("<option value=\"na\">Year</option>");
        for (int i = 2013; i > 1900; i--) {
            sb.append("<option value=\"" + i + "\"");
            if (cal != null && cal.get(Calendar.YEAR) == i) {
                sb.append(" selected>" + i + "</option>");
            } else {
                sb.append(">" + i + "</option>");
            }
        }
        sb.append("</select>");
        
        return sb.toString();
    }
    
    public static Date laterDate (Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return null;
        } else if (d1 != null && d2 == null) {
            return d1;
        } else if (d1 == null && d2 != null) {
            return d2;
        } else {
            if (d1.getTime() > d2.getTime()) {
                return d1;
            } else {
                return d2;
            }
        }
    }
    
    public static boolean isDateNull (Date d) {
        if (d == null) {
            return true;
        } else if (d.getTime() == 0l) {
            return true;
        } else {
            return false;
        }
            
    }
    
}
