package com.films.management;
import com.films.management.routes.Routes;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;

import java.io.File;

import static spark.Spark.*;

public class Application {
    public static ObjectContext context;
    public static void main(String[] args) {
        ServerRuntime cayenneRuntime = ServerRuntime.builder().addConfig("cayenne-project.xml").build();
        context = cayenneRuntime.newContext();
        externalStaticFileLocation(new File("").getAbsolutePath()+"/public");
        port(8000);
        Routes.map();
    }
}
