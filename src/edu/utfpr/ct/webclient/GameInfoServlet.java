/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utfpr.ct.webclient;

import be.ceau.chart.LineChart;
import be.ceau.chart.data.LineData;
import be.ceau.chart.dataset.LineDataset;
import be.ceau.chart.enums.HoverMode;
import be.ceau.chart.options.Hover;
import be.ceau.chart.options.Legend;
import be.ceau.chart.options.LineOptions;
import be.ceau.chart.options.Title;
import be.ceau.chart.options.Tooltips;
import be.ceau.chart.options.elements.Line;
import be.ceau.chart.options.elements.LineElements;
import be.ceau.chart.options.scales.LinearScale;
import be.ceau.chart.options.scales.LinearScales;
import be.ceau.chart.options.scales.ScaleLabel;
import be.ceau.chart.options.ticks.LinearTicks;
import edu.utfpr.ct.datamodel.Game;
import edu.utfpr.ct.datamodel.Node;
import edu.utfpr.ct.localization.HostLocalizationKeys;
import edu.utfpr.ct.localization.HostLocalizationManager;
import edu.utfpr.ct.localization.Localize;
import edu.utfpr.ct.util.ChartJSUtils;
import java.io.IOException;
import java.math.BigDecimal;
import javafx.scene.paint.Color;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet(
        name = "BeerGameHostService-Information",
        urlPatterns = {"/info"}
)
public class GameInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getRemoteAddr().equals("127.0.0.1")) {
//            System.out.println(req.getRemoteAddr());
            return;
        }

        String gameName = req.getParameter("game-name");
        String legend = req.getParameter("no-legend");

        ActionService service = null;
        Object obj = req.getServletContext().getAttribute("action-service");

        if (obj != null && obj instanceof ActionService) {
            service = (ActionService) obj;
        }

        if (service != null) {
            Game g = service.getGameInfo(gameName);

            if (g == null) {
                g = service.getReportInfo(gameName);
            }

            if (g != null) {

                boolean noLegend = false;

                try {
                    noLegend = Boolean.parseBoolean(legend);
                } catch (Exception ex) {
                }

                Localize loc = HostLocalizationManager.getInstance().getClientFor(HostLocalizationManager.getInstance().getLang().get());

                int maxV = 0;

                JSONObject chartInfo = new JSONObject();
                chartInfo.put("type", "line");
                
                JSONObject chartData = new JSONObject();
                chartInfo.put("data", chartData);
                
                JSONArray chartLabels = new JSONArray();
                chartData.put("labels", chartLabels);
                
                for (Integer k = 1; k <= g.realDuration; k++) {
                    chartLabels.add(k.toString());
                }
                
                JSONArray chartDatasets = new JSONArray();
                chartData.put("datasets", chartDatasets);
                
                JSONObject chartOptions = new JSONObject();
                chartInfo.put("options", chartOptions);
                
                for (int k = ((g.supplyChain.length) / (g.deliveryDelay + 1)) - 1; k >= 0 ; k--) {
                    JSONObject dataset = new JSONObject();
                    Node n = ((Node) g.supplyChain[k * (g.deliveryDelay + 1)]);
                    
                    dataset.put("label", loc.getTextFor(HostLocalizationKeys.CHART_OR_FUNCTION_AX + n.function.getPosition()));
                    dataset.put("fill", false);
                    dataset.put("backgroundColor", ChartJSUtils.COLORS_S[k + 1]);
                    dataset.put("borderColor", ChartJSUtils.COLORS_S[k + 1]);
                    
                    JSONArray data = new JSONArray();
                    int[] vals = new int[n.playerMove.size()];

                    for (int l = 0; l < vals.length; l++) {
                        vals[l] = n.playerMove.get(l);
                        data.add(n.playerMove.get(l));

                        if (maxV < vals[l]) {
                            maxV = vals[l];
                        }
                    }
                    
                    dataset.put("data", data);
                    chartDatasets.add(dataset);
                }
                
                JSONObject dataset = new JSONObject();
                dataset.put("label", loc.getTextFor(HostLocalizationKeys.CHART_OR_FUNCTION_AX + "0"));
                dataset.put("fill", false);
                dataset.put("backgroundColor", ChartJSUtils.COLORS_S[0]);
                dataset.put("borderColor", ChartJSUtils.COLORS_S[0]);
                JSONArray data =  new JSONArray();
                for(int k = 0; k < g.demand.length; k++){
                    data.add(g.demand[k]);
                    
                    if (maxV < g.demand[k]) {
                        maxV = g.demand[k];
                    }
                    
                }
                dataset.put("data", data);
                
                chartDatasets.add(dataset);

                chartOptions.put("responsive", "true");
                
                JSONObject ax = new JSONObject();
                ax.put("display", "true");
                ax.put("text", loc.getTextFor(HostLocalizationKeys.CHART_OR_TITLE));
                
                chartOptions.put("title", ax);
                
                ax = new JSONObject();
                ax.put("display", !noLegend);
                ax.put("reverse", "true");
                
                chartOptions.put("legend", ax);
                
                ax = new JSONObject();
                ax.put("mode", "index");
                
                chartOptions.put("tooltips", ax);
                
                ax = new JSONObject();
                ax.put("mode", "nearest");
                
                chartOptions.put("hover", ax);
                
                JSONObject scales = new JSONObject();
                chartOptions.put("scales", scales);
                
                JSONArray axes = new JSONArray();
                scales.put("xAxes", axes);
                
                ax = new JSONObject();
                ax.put("display", "true");
                
                JSONObject scalLabel = new JSONObject();
                scalLabel.put("display", "true");
                scalLabel.put("labelString", loc.getTextFor(HostLocalizationKeys.CHART_OR_LABEL_X));
                ax.put("scaleLabel", scalLabel);
                
                axes.add(ax);
                
                axes = new JSONArray();
                scales.put("yAxes", axes);
                
                ax = new JSONObject();
                ax.put("display", "true");
                
                scalLabel = new JSONObject();
                scalLabel.put("display", "true");
                scalLabel.put("labelString", loc.getTextFor(HostLocalizationKeys.CHART_OR_LABEL_Y));
                ax.put("scaleLabel", scalLabel);
                
                JSONObject ticks = new JSONObject();
                ax.put("ticks", ticks);
                
                ticks.put("beginAtZero", true);
                ticks.put("suggestedMax", maxV + 1);
                
                axes.add(ax);
                
                JSONObject elements = new JSONObject();
                chartOptions.put("elements", elements);
                
                ax = new JSONObject();
                elements.put("line", ax);
                
                ax.put("tension", 0.0);
                

                resp.setContentType("text/html");
                resp.setCharacterEncoding("UTF-8");

                StringBuilder html = new StringBuilder();

                html.append("<!DOCTYPE html>");
                html.append("<html> <head>");
                html.append("<script src='/resources/chartjs/Chart.js'></script>");
                html.append("<style> canvas { -moz-user-select: none; -webkit-user-select: none; -ms-user-select: none; } </style></head>");
                html.append("<body><div style=\"width: 95%;\"><canvas id=\"chart\"></canvas> </div> <script>"
                        + "var ctx = document.getElementById(\"chart\");"
                        + "var myChart = new Chart(ctx, ");
                html.append(chartInfo.toJSONString());
                html.append(");</script>");
                html.append("</body></html>");

                resp.setStatus(200);
                resp.getOutputStream().write(html.toString().getBytes());
                resp.getOutputStream().flush();
            }
        } else {
            resp.setContentType("text/html");
            resp.getOutputStream().write("<!DOCTYPE html><html> <head></head><bodystyle='background-color: red;'><h1>Erro</h1></body></html>".getBytes());
            resp.getOutputStream().flush();
        }

        resp.getOutputStream().close();
    }

}