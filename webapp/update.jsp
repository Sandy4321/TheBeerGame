<%@page import="java.io.*" %>
<%@page import="java.net.*" %>
<%@page import="org.json.simple.*" %>
<%@page import="org.json.simple.parser.*" %>
<%
    if(session.getAttribute("USER-ID") == null || ((String)session.getAttribute("USER-ID")).isEmpty()){
        response.sendRedirect("/check_in.jsp");
        return;
    }
    
    String recvbuff = "";
    
    if(request.getMethod().equals("POST")){
        String user = session.getAttribute("USER-ID").toString();
        String game = session.getAttribute("LOGGED_GAME").toString();
        String func = request.getParameter("function").toString();
        String week = request.getParameter("week").toString();
        
        if(game == null || game.isEmpty()){
            response.sendRedirect("/choose_room.jsp");
            return;
        }
        
        String recv;
        URL checkin_json = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), "/update?game-name=" + URLEncoder.encode(game, "UTF-8").replace("+", "%20") + "&player-name=" + URLEncoder.encode(user, "UTF-8").replace("+", "%20") + "&table-function=" + URLEncoder.encode(func, "UTF-8").replace("+", "%20") + "&table-week=" + URLEncoder.encode(week, "UTF-8").replace("+", "%20"));
        HttpURLConnection urlcon = (HttpURLConnection)checkin_json.openConnection();
        urlcon.setRequestMethod("GET");
        BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

        while ((recv = buffread.readLine()) != null)
            recvbuff += recv;
        buffread.close();
    }else{
        response.sendRedirect("/choose_room.jsp");
    }
    
%><%=recvbuff %>