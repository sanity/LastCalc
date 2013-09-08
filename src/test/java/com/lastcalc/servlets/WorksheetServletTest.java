package com.lastcalc.servlets;


import com.lastcalc.TokenList;
import com.lastcalc.db.Worksheet;
import org.jscience.mathematics.number.Number;
import org.junit.Assert;
import org.junit.Test;


import java.io.IOException;
import java.util.HashMap;

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
public class WorksheetServletTest {


    @Test
    public void testFunctionCreation() throws IOException {

        final Worksheet worksheet=new Worksheet();

        {
            WorksheetServlet.WorksheetRequest worksheetRequest=new WorksheetServlet.WorksheetRequest();

            worksheetRequest.questions=new HashMap<Integer, String>();
            worksheetRequest.questions.put(1,"double X = X*2");


            WorksheetServlet.getWorksheetResponse(worksheet, worksheetRequest, "");

        }

        {
            WorksheetServlet.WorksheetRequest worksheetRequest=new WorksheetServlet.WorksheetRequest();

            worksheetRequest.questions=new HashMap<Integer, String>();
            worksheetRequest.questions.put(2,"double 5");
            WorksheetServlet.getWorksheetResponse(worksheet, worksheetRequest, "");
        }


        final TokenList result=worksheet.qaPairs.get(1).answer;


        Assert.assertEquals(1, result.size());

        Assert.assertTrue("Assert that " + result.get(0).getClass() + " is a Number", result.get(0) instanceof org.jscience.mathematics.number.Number);
        double value = ((Number) result.get(0)).doubleValue();
        Assert.assertEquals(10.0, value,0.01);

        System.out.println("worksheet servlet test finished");

    }

}
