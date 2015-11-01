/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package if4072.sentimentanalysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author christangga
 */
public class IF4072SentimentAnalysis {

    /**
     * @param args the command line arguments
     * @throws twitter4j.TwitterException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws TwitterException, FileNotFoundException, InterruptedException {

        PrintStream out = new PrintStream(new FileOutputStream("data/" + "gojek" + ".csv"));
        System.setOut(out);

        String proxyUsername = "";
        String proxyPassword = "";
        String queryString = "";

        if (args[0].equals("-p")) {
            proxyUsername = args[1];
            proxyPassword = args[2];
            for (int i = 3; i < args.length; ++i) {
                queryString += args[i] + " ";
            }
        } else {
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-p")) {
                    proxyUsername = args[i + 1];
                    proxyPassword = args[i + 2];
                    break;
                } else {
                    queryString += args[i] + " ";
                }
            }
        }
        queryString = queryString.trim();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("T9NUdDX6v1h9ePI61RKi4g");
        cb.setOAuthConsumerSecret("xi4EIC9so6e6n8oOM3ggx2zbayj3JWhSpEj165bEJU");
        cb.setOAuthAccessToken("178369831-97z3fDZvMH0e8Ne0ot7WzFXayAPSU2eOx9VzLr4y");
        cb.setOAuthAccessTokenSecret("KwTHAIi2bWAVWOPtNM4NoYaiAwLvrkGWqWHv6jGBKffF5");

        if (args[0].equals("-p")) {
            cb.setHttpProxyHost("cache.itb.ac.id");
            cb.setHttpProxyPort(8080);
            cb.setHttpProxyUser(proxyUsername);
            cb.setHttpProxyPassword(proxyPassword);
        }

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        Query query = new Query(queryString + " +exclude:retweets");
        query.setCount(100);

        HashSet<String> tweetSet = new HashSet<>();
        long lastID = Long.MAX_VALUE;
        int totalRequest = 0;
        
        System.out.println("\"content\",\"original_id\",\"from\",\"date_created\",\"sentiment\",\"original_sentiment\"");
        while (tweetSet.size() < 10000) {
            QueryResult result = twitter.search(query);
            totalRequest++;
            
            for (Status status : result.getTweets()) {
                List<String> tweetStatus = new LinkedList<>(Arrays.asList(status.getText().replace("\"", "'").replace(".", "").replace(":", "").replace("-", "").replace(",", "").replace("\n", " ").replace("\n\r", " ").replace("\r", " ").split(" ")));
                for (int j = tweetStatus.size() - 1; j >= 0; --j) {
                    if (tweetStatus.get(j).contains("http") || tweetStatus.get(j).startsWith("#")) {
                        // System.out.println("tmp" + String.join(" ", tweetStatus));
                        tweetStatus.remove(j);
                    }
                }

                // System.out.println(tweetStatus.toString());
                if (tweetSet.add(String.join(" ", tweetStatus))) {
                    // tweetSet.add(String.join(" ", tweetStatus));
                    // System.out.println(String.join(" ", tweetStatus));
                    System.out.println("\"" + status.getText() + "\",\"" + status.getId() + "\",\"" + status.getUser().getScreenName() + "\",\"" + getDateString(status.getCreatedAt()) + "\",\"\",\"\"");
                }

                lastID = status.getId();
            }
            query.setMaxId(lastID - 1);
            
            if (totalRequest >= 400) {
                TimeUnit.MINUTES.sleep(16);
            }
        }
    }

    public static String getDateString(Date d) {
        String dateString = "";

        dateString += String.format("%02d", d.getYear() + 1900) + "-";
        dateString += String.format("%02d", d.getMonth() + 1) + "-";
        dateString += String.format("%02d", d.getDate()) + " ";
        dateString += String.format("%02d", d.getHours()) + ":";
        dateString += String.format("%02d", d.getMinutes()) + ":";
        dateString += String.format("%02d", d.getSeconds());

        return dateString;
    }

}
