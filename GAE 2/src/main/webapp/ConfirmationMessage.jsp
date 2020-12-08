<%@page import="ds.gae.view.JSPSite"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% session.setAttribute("currentPage", JSPSite.LOGIN); %>
<% String renter = null; %>

<%@include file="_header.jsp"%>

<div class="frameDiv" style="margin: 150px 280px;">
    <h2>Quotes Submission Confirmed</h2>
    <div>
        Your submission has been received. You can see the current status on the overview page
    </div>
</div>

<%@include file="_footer.jsp"%>
