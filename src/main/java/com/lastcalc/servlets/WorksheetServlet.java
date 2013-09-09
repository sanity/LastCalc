/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU Affero General Public License for more
 * details.
 ******************************************************************************/
package com.lastcalc.servlets;

import com.google.common.collect.Maps;
import com.googlecode.objectify.Objectify;
import com.lastcalc.*;
import com.lastcalc.db.DAO;
import com.lastcalc.db.Line;
import com.lastcalc.db.Worksheet;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.parsers.currency.Currencies;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class WorksheetServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {

    }

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final WorksheetRequest request = Misc.gson.fromJson(req.getReader(), WorksheetRequest.class);

        final String requestURI = req.getRequestURI();



        final Objectify obj = DAO.begin();

        final Worksheet worksheet = obj.load().type(Worksheet.class).id(request.worksheetId).get();
        //System.out.println("Worksheet: "+Misc.gsonpp.toJson(worksheet));
        //System.out.println("Request: "+Misc.gsonpp.toJson(request));
        if (worksheet == null) {
            resp.sendError(404);
            return;
        }


        //System.out.println("Request to worksheet: \n"+Misc.gson.toJson(request));

        final WorksheetResponse response = getWorksheetResponse(worksheet, request, requestURI);
        //System.out.println("WorksheetResponse: "+Misc.gson.toJson(response));



        obj.save().entity(worksheet);

        resp.setContentType("application/json; charset=UTF-8");
        Misc.gson.toJson(response, resp.getWriter());

        if (Currencies.shouldUpdate()) {
            Currencies.updateExchangeRates();
        }
    }

    protected static WorksheetResponse getWorksheetResponse(Worksheet worksheet, WorksheetRequest request, String requestURI) throws IOException {
        final ArrayList<Line> qaPairs = worksheet.qaPairs;

        if (request.questions != null) {
            //int earliestModified = Integer.MAX_VALUE;
            final TreeMap<Integer, String> orderedQuestions = Maps.newTreeMap();
            orderedQuestions.putAll(request.questions);
            for (final Entry<Integer, String> question : orderedQuestions.entrySet()) {
                final int pos = question.getKey() - 1;
                if (pos < qaPairs.size()) {
                    final Line qaPair = qaPairs.get(pos);
                    qaPair.lineNum=question.getKey();
                    qaPair.question = question.getValue();
                    //earliestModified = Math.min(earliestModified, pos);
                } else {
                    qaPairs.add(new Line(question.getKey(),question.getValue(), null));
                }
            }
            for (int x = 0; x < qaPairs.size(); x++) {
                final Line qaPair = qaPairs.get(x);
                qaPair.answer = null;
            }
            // Remove any qaPairs that have been removed from the browser DOM
            if (!orderedQuestions.isEmpty()) {
                while (qaPairs.size() > orderedQuestions.lastKey()) {
                    qaPairs.remove(qaPairs.size() - 1);
                }
            }
        }

        // Recompute worksheet
        final SequentialParser seqParser = SequentialParser.create();

        for (final Line qap : qaPairs) {
            if (qap.question.trim().length() == 0) {
                qap.answer = TokenList.createD();
            } else {
                if (qap.answer == null) {
                    qap.answer = seqParser.parseNext(Tokenizer.tokenize(qap.question));//original ian's code
                } else {
                    seqParser.processNextAnswer(qap.answer);
                }
            }
        }

        worksheet.definedParsers = seqParser.getUserDefinedParsers().getParsers();

        final WorksheetResponse response = new WorksheetResponse();
        if (request.getRecognizedWords) {
            response.recognizedWords = SequentialParser.recognizedWords;
        }

        response.answers = Maps.newHashMap();
        response.answerTypes = Maps.newHashMap();
        response.variables = seqParser.getUserDefinedKeywordMap();

        for (int x = 0; x < qaPairs.size(); x++) {
            final TokenList answer = qaPairs.get(x).answer;
            final TokenList strippedAnswer = seqParser.stripUDF(answer);
            
            if(qaPairs.get(x).lineNum==x+1){

                System.out.println("lineNum properly matches qaPairs index");
                
                response.answers.put(x + 1, Renderers.toHtml(requestURI, strippedAnswer) .toString() );
                response.answerTypes.put(x + 1, getAnswerType(strippedAnswer));
            }
            else{

                System.out.println("lineNum DOES NOT MATCH qaPairs index, lineNum: "+qaPairs.get(x).lineNum+" , qaPairs index: " +(x+1)+" ...............................\n\n");

                response.answers.put(qaPairs.get(x).lineNum, Renderers.toHtml(requestURI, strippedAnswer) .toString() );
                response.answerTypes.put(qaPairs.get(x).lineNum, getAnswerType(strippedAnswer));
            }

        }
        return response;
    }

    public static AnswerType getAnswerType(final TokenList answer) {
        if (answer.size() == 1) {
            if (answer.get(0) instanceof UserDefinedParser)
                return AnswerType.FUNCTION;
            else if (answer.get(0) instanceof Collection) {
                for (final Object o : ((Collection<?>) answer.get(0))) {
                    if (!(o instanceof UserDefinedParser))
                        return AnswerType.NORMAL;
                }
                return AnswerType.FUNCTION;
            }
        }
        return AnswerType.NORMAL;
    }

    public static class WorksheetRequest {
        public boolean getRecognizedWords = false;

        public String worksheetId;

        public Map<Integer, String> questions;
    }

    public static class WorksheetResponse {
        Set<String> recognizedWords;

        Map<Integer, String> answers;

        Map<Integer, AnswerType> answerTypes;

        Map<String, Integer> variables;
    }

    public enum AnswerType {
        NORMAL, FUNCTION, ERROR
    }
}
