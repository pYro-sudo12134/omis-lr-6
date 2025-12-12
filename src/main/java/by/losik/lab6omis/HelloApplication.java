package by.losik.lab6omis;

import by.losik.lab6omis.resource.CacheResource;
import by.losik.lab6omis.resource.HelloResource;
import by.losik.lab6omis.resource.general.types.AnalysisResource;
import by.losik.lab6omis.resource.general.types.RequestResource;
import by.losik.lab6omis.resource.general.types.ResponseResource;
import by.losik.lab6omis.resource.general.types.SensorDataResource;
import by.losik.lab6omis.resource.general.types.SensorResource;
import by.losik.lab6omis.resource.general.types.SolutionResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class HelloApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(HelloResource.class);
        classes.add(CacheResource.class);
        classes.add(AnalysisResource.class);
        classes.add(RequestResource.class);
        classes.add(ResponseResource.class);
        classes.add(SensorDataResource.class);
        classes.add(SensorResource.class);
        classes.add(SolutionResource.class);
        return classes;
    }
}