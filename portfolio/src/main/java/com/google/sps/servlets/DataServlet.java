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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that posts and retrieves comments from Datastore. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * Gets the comments, in JSON format, taken from user input in the form from the HTML,
   * which are stored in the messages variable.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create a query and prepare it with the data stored in Datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery preparedQuery = datastore.prepare(query);

    // Set maximum number of comments to be included in the response.
    int numOfComments = Integer.parseInt(request.getParameter("limit"));
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withLimit(numOfComments));

    // Add all queried comments from Datastore
    // to an List of type Comment.
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results) {
      long id = entity.getKey().getId();
      String content = (String) entity.getProperty("content");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, content, timestamp);
      comments.add(comment);
    }
    
    // Convert the comments List to JSON format.
    Gson gson = new Gson();

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(comments));
  }

  /** Posts a comment retrieved from the form input adding it to the messages variable. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the comment from the form and add it to the array.
    String content = request.getParameter("text-input");
    long timestamp = System.currentTimeMillis();

    // Create an Entity that holds the comment and the time it was created
    // and store it in Datastore.
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }
}
