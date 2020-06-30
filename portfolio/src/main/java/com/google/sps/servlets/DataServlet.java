// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some placeholder comment saying 'Hello, Omar!'. TODO(oumontiel): modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * List of messages to be fetched from this Data Servlet
   * @private {ArrayList<String>}
   */
  private ArrayList<String> messages;

  @Override
  public void init() {
    messages = new ArrayList<>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(convertToJson(messages));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /** Get the message from the form and add it to the array */
    String message = request.getParameter("text-input");
    messages.add(message);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Converts an array into a JSON string using manual String concatentation.
   */
  private String convertToJson(ArrayList messages) {
    String json = "{";
    int i;
    for (i = 0; i < messages.size() - 1; i++) {
      json += "\"string" + i + "\": ";
      json += "\"" + messages.get(i) + "\"";
      json += ", ";
    }
    json += "\"string" + i + "\": ";
    json += "\"" + messages.get(i) + "\"";
    json += "}";
    return json;
  }
}
