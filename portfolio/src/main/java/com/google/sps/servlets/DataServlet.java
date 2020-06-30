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

  private ArrayList<String> messages;

  @Override
  public void init() {
    messages = new ArrayList<>();
    messages.add("Once upon a time...");
    messages.add("Twice upon two times...");
    messages.add("Thrice upon three times...");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(convertToJson(messages));
  }

  /**
   * Converts an array into a JSON string using manual String concatentation.
   */
  private String convertToJson(ArrayList messages) {
    String json = "{";
    json += "\"string1\": ";
    json += "\"" + messages.get(0) + "\"";
    json += ", ";
    json += "\"string2\": ";
    json += "\"" + messages.get(1) + "\"";
    json += ", ";
    json += "\"string3\": ";
    json += "\"" + messages.get(2) + "\"";
    json += "}";
    return json;
  }
}
