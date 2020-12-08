<%@page import="ds.gae.view.JSPSite"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% session.setAttribute("currentPage", JSPSite.CONFIRM_QUOTES_RESPONSE); %>
<% String renter = (String) session.getAttribute("renter"); %>

<%@include file="_header.jsp"%>

<div class="frameDiv" style="margin: 150px 150px;">
    <h2>Confirmation</h2>
    <div class="group">
        <p>
           <%=renter%>, your quotes have been received
        </p>
    </div>
</div>

<%@include file="_footer.jsp"%>
