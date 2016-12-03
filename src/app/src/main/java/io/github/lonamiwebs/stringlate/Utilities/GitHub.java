package io.github.lonamiwebs.stringlate.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.lonamiwebs.stringlate.Interfaces.Callback;
import io.github.lonamiwebs.stringlate.Tasks.DownloadJSONTask;

// Static GitHub API interface that uses AsyncTasks
public class GitHub {

    //region Members

    private static final String API_URL = "https://api.github.com/";

    //endregion

    //region Private methods

    // Calls the given function with GET method and invokes callback() as result
    private static void gCall(final String call,
                              final Callback<Object> callback) {
        new DownloadJSONTask() {
            @Override
            protected void onPostExecute(Object jsonObject) {
                callback.onCallback(jsonObject);
                super.onPostExecute(jsonObject);
            }
        }.execute(API_URL+call);
    }

    // Calls the given function with POST method and the given
    // parameters and invokes callback() as result
    private static void gCall(final String call, String data,
                              final Callback<Object> callback) {
        new DownloadJSONTask("POST", data) {
            @Override
            protected void onPostExecute(Object jsonObject) {
                callback.onCallback(jsonObject);
                super.onPostExecute(jsonObject);
            }
        }.execute(API_URL+call);
    }

    //endregion

    //region Public methods

    // Determines whether the given combination of owner/repo is OK or not
    public static void gCheckOwnerRepoOK(String owner, String repo,
                                  final Callback<Boolean> callback) {
        gCall(String.format("repos/%s/%s", owner, repo), new Callback<Object>() {
            @Override
            public void onCallback(Object object) {
                callback.onCallback(object != null);
            }
        });
    }

    // Looks for 'query' in 'owner/repo' repository's files and
    // returns a JSON with the files for which 'filename' also matches
    public static void gFindFiles(String owner, String repo,
                                  String query, String filename,
                                  final Callback<Object> callback) {
        gCall(String.format("search/code?q=%s+repo:%s/%s+filename:%s",
                query, owner, repo, filename), callback);
    }

    public static void gCreateGist(String description, boolean isPublic,
                                   String filename, String content,
                                   final Callback<Object> callback) {
        try {
            JSONObject params = new JSONObject();

            if (description != null && !description.isEmpty())
                params.put("description", description);

            params.put("public", isPublic);

            JSONObject fileObject = new JSONObject();
            fileObject.put("content", content);

            JSONObject filesObject = new JSONObject();
            filesObject.put(filename, fileObject);

            params.put("files", filesObject);

            gCall("gists", params.toString(), callback);
        }
        catch (JSONException e) {
            // Won't happen
            e.printStackTrace();
        }
    }

    //endregion
}
