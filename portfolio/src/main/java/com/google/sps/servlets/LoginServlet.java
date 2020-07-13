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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that creates login or logout URL and sends it as response.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  /**
   * Instantiates a UserAuthenticationData object and returns it in JSON format.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create variables needed to instantiate a UserAuthenticationData object.
    Boolean loggedIn = false;
    String authenticationUrl = ""; // URL used for either login or logout.
    String userEmail = "";
    String nickname = "";
    String redirectUrl = "/"; // Both login and logout redirect to the same URL.

    // Define values for user variables depending on login status.
    if (userService.isUserLoggedIn()) {
      loggedIn = true;
      userEmail = userService.getCurrentUser().getEmail();
      nickname = getUserNickname(userService.getCurrentUser().getUserId());
      if (nickname == null) {
        // If logged in user has no nickname, redirect to nickname setup page.
        nickname = "";
        authenticationUrl = "/nickname.html";
      } else {
        // If logged in user has a nickname, set logout URL.
        authenticationUrl = userService.createLogoutURL(redirectUrl);
      }
    } else {
      authenticationUrl = userService.createLoginURL(redirectUrl);
    }

    // Create new user.
    UserAuthenticationData userAuthenticationData =
        new UserAuthenticationData(loggedIn, authenticationUrl, userEmail, nickname);

    // Convert the user to JSON format and return it as response.
    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(userAuthenticationData));
  }

  /**
   * Returns the nickname of the user with id, or null if the user has not set a nickname.
   */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
}
