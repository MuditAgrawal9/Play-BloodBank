package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class HealthController extends Controller {

    public Result health() {
        return ok("Healthy");
    }

}