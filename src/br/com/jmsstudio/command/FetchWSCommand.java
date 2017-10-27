package br.com.jmsstudio.command;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

public class FetchWSCommand implements Callable<String> {

    private PrintStream printStream;

    public FetchWSCommand(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public String call() throws Exception {
        System.out.println("Fetching data from a web api");

        this.printStream.println("Making request to web api");

        URL url = new URL("http://api.icndb.com/jokes/random");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder result = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        this.printStream.println("Request processed with success");

        System.out.println("Finished fetching data from a web api");

        String json = result.toString();

        Map map = parseJsonToMap(json);

        String returnText = "";

        if (map.get("type").equals("success")) {
            returnText = "Data received: " + ((Map) map.get("value")).get("joke").toString();
        }

        return returnText;
    }

    private Map parseJsonToMap(String json) throws ScriptException {
        ScriptEngine javascript = new ScriptEngineManager().getEngineByName("javascript");
        return (Map) javascript.eval("Java.asJSONCompatible(" + json + ")");
    }
}
