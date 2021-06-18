package com.ellalan.certifiedparent.parsers;


import android.util.Xml;

import com.ellalan.certifiedparent.model.Question;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class QuestionInfoParser {

    private static final String ns = null;


    public ArrayList<Question> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);

        } finally {
            in.close();
        }
    }

    private ArrayList<Question> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        ArrayList<Question> questions = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "questions");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("question")) {

                Question course = readCourse(parser);
                questions.add(course);

            } else {

                skip(parser);
            }
        }
        return questions;
    }

    private Question readCourse(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "question");

        Question newCourse = new Question();
        String Q;
        String A;
        String B;
        String C;
        String D;
        String R;
        String I;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "Q":
                    Q = readQuestion(parser);
                    newCourse.setQ(Q);
                    break;
                case "A":
                    A = readA(parser);
                    newCourse.setA(A);
                    break;
                case "B":
                    B = readB(parser);
                    newCourse.setB(B);
                    break;
                case "C":
                    C = readC(parser);
                    newCourse.setC(C);
                    break;
                case "D":
                    D = readDescription(parser);
                    newCourse.setD(D);
                    break;
                case "R":
                    R = readResult(parser);
                    newCourse.setR(R);
                    break;
                case "I":
                    I = readImage(parser);
                    newCourse.setI(I);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return newCourse;
    }

    private String readQuestion(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Q");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Q");
        return title;
    }

    private String readA(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "A");
        String subtitle = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "A");
        return subtitle;
    }

    private String readB(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "B");
        String address = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "B");
        return address;
    }

    private String readC(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "C");
        String city = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "C");
        return city;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "D");
        String state = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "D");
        return state;
    }

    private String readResult(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "R");
        String subtitle = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "R");
        return subtitle;
    }
    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "I");
        String subtitle = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "I");
        return subtitle;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
