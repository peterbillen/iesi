package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import io.metadew.iesi.script.operation.ActionSelectOperation;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Negotiator {

    public Negotiator() {
        // Set the environment
    }

    public void submitScenario(String eng_cfg, String env_nm, int app_id, int scenario_id) {
        this.submitRequest("SCENARIO", eng_cfg, env_nm, app_id, scenario_id);
    }

    public void submitTest(String eng_cfg, String env_nm, int test_id) {
        this.submitRequest("TEST", eng_cfg, env_nm, -1, test_id);
    }

    public void submitTestSet(String eng_cfg, String env_nm, int test_id) {
        this.submitRequest("TESTSET", eng_cfg, env_nm, -1, test_id);
    }

    @SuppressWarnings("unused")
    private void submitRequest(String request_type, String eng_cfg, String env_nm, int context_id, int scope_id) {
        String QueryString = "insert into que_request (que_id, request_type, eng_cfg,env_nm,context_id,scope_id,prc_id) values ((select ifnull(max(que_id),0) + 1 as QUE_ID from  que_request),'"
                + request_type + "','" + eng_cfg + "','" + env_nm + "'," + context_id + "," + scope_id + ",-1)";
    }

    public void doStg() {
        // Run a task specified by a Supplier object asynchronously
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    runScript();
                } catch (InterruptedException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | ScriptExecutionBuildException | SQLException e) {
                    throw new IllegalStateException(e);
                }
                return "Result of the asynchronous computation";
            }
        });

        // Block and get the result of the Future
        String result;
        try {
            result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void runScript() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, ScriptExecutionBuildException, SQLException {
        // Create the framework instance
        FrameworkInstance.getInstance().init(new FrameworkInitializationFile(), new FrameworkExecutionContext(new Context("negotiator", "")));

        // Get the Script
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration();
        Script script = scriptConfiguration.get("S2").get();



        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
                .script(script)
                .actionSelectOperation(new ActionSelectOperation(""))
                .environment("DEV")
                .executionControl(new ExecutionControl())
                .exitOnCompletion(true)
                .build();

        // Execute the Script
        scriptExecution.execute();

    }

}
