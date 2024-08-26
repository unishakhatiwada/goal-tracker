package Utils;

public class Routes {
    // User-related routes
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    // Goal-related routes
    public static final String LIST_GOAL = "/goals/all/";
    public static final String CREATE_GOAL = "/goals/create";
    public static final String GET_GOAL_DETAIL = "/goals/detail/"; // Changed to get specific goal detail
    public static final String UPDATE_GOAL = "/goals/update";
    public static final String DELETE_GOAL = "/goals/delete";


    // Task-related routes
    public static final String CREATE_TASK = "/tasks/create";
    public static final String GET_TASK_DETAIL = "/tasks/detail"; // Changed to get specific task detail
    public static final String UPDATE_TASK = "/tasks/update";
    public static final String DELETE_TASK = "/tasks/delete";
}
