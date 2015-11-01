/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package if4072.sentimentanalysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
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
     */
    public static void main(String[] args) throws TwitterException, FileNotFoundException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        if (args[0].equals("-p")) {
            cb.setHttpProxyHost("cache.itb.ac.id");
            cb.setHttpProxyPort(8080);
            cb.setHttpProxyUser(args[1]);
            cb.setHttpProxyPassword(args[2]);
        }
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("T9NUdDX6v1h9ePI61RKi4g")
                .setOAuthConsumerSecret(
                        "xi4EIC9so6e6n8oOM3ggx2zbayj3JWhSpEj165bEJU")
                .setOAuthAccessToken(
                        "178369831-97z3fDZvMH0e8Ne0ot7WzFXayAPSU2eOx9VzLr4y")
                .setOAuthAccessTokenSecret(
                        "KwTHAIi2bWAVWOPtNM4NoYaiAwLvrkGWqWHv6jGBKffF5");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        String queryString = "";
        if (args[0].equals("-p")) {
            for (int i = 3; i < args.length; ++i) {
                queryString += args[i] + " ";
            }
        } else {
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-p")) {
                    break;
                } else {
                    queryString += args[i] + " ";
                }
            }
        }
        queryString = queryString.trim();

        Query query = new Query(queryString + " +exclude:retweets");
        query.setCount(100);
        QueryResult result = twitter.search(query);
        
        try (PrintStream out = new PrintStream(new FileOutputStream("data/output.csv"))) {
            System.setOut(out);

            System.out.println("\"content\",\"original_id\",\"from\",\"date_created\",\"sentiment\",\"original_sentiment\"");
            for (int i = 0; i < 200; ++i) {
                result.getTweets().stream().forEach((status) -> {
                    System.out.println("\"" + status.getText() + "\",\"" + status.getId() + "\",\"" + status.getUser().getScreenName() + "\",\"" + getDateString(status.getCreatedAt()) + "\",\"\",\"\"");
                });
                result.nextQuery();
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
