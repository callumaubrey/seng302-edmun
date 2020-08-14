/**
 * Contains methods required for validating and searching activity data through the backend api
 */

export default {

    /**
     * Returns a promise from an api request of search activities given the parameters otherwise throws an
     * error if any of the arguments are invalid.
     * @param instance Axios Instance for making api request with credentials
     * @param search_query String name matching text for an activity
     * @param types List of types an activity must match with, either or/and based on types_method_and
     * @param types_method_and Boolean if types must matched by conjunction or disjunction. True if conjunction.
     * @param hashtags List of hashtags that the activities must match with, either or/and based on hashtags_method_and
     * @param hashtags_method_and Boolean if hashtags must be matched by conjunction or disjunction. True if conjunction.
     * @param activity_mode_filter String for activity mode filtering must match "all", "continuous", "duration"
     * @param start_date Date if activity_mode_filter is "Duration" then this filters any activities that start before this Date.
     * @param end_date Date if activity_mode_filter is "Duration" then this filters any activities that end after this Date.
     * @param pagination_offset Integer offsets search results
     * @param pagination_limit Integer limits number of results returned
     */
    searchActivities: function(instance,
                               search_query=undefined,
                               types = [],
                               types_method_and = true,
                               hashtags = [],
                               hashtags_method_and = true,
                               activity_mode_filter="all",
                               start_date=undefined,
                               end_date=undefined,
                               pagination_offset=0,
                               pagination_limit=10) {

        let query_params_str = this.getSearchActivitiesQueryParams(search_query, types, types_method_and, hashtags,
            hashtags_method_and, activity_mode_filter, start_date, end_date, pagination_offset, pagination_limit);

        return instance.get('/activities?' + query_params_str);
    },

    /**
     * Returns a promise from an api request of search activities count given the parameters otherwise throws an
     * error if any of the arguments are invalid.
     * @param instance Axios Instance for making api request with credentials
     * @param search_query String name matching text for an activity
     * @param types List of types an activity must match with, either or/and based on types_method_and
     * @param types_method_and Boolean if types must matched by conjunction or disjunction. True if conjunction.
     * @param hashtags List of hashtags that the activities must match with, either or/and based on hashtags_method_and
     * @param hashtags_method_and Boolean if hashtags must be matched by conjunction or disjunction. True if conjunction.
     * @param activity_mode_filter String for activity mode filtering must match "all", "continuous", "duration"
     * @param start_date Date if activity_mode_filter is "Duration" then this filters any activities that start before this Date.
     * @param end_date Date if activity_mode_filter is "Duration" then this filters any activities that end after this Date.
     * @param pagination_offset Integer offsets search results
     * @param pagination_limit Integer limits number of results returned
     */
    searchActivitiesPageCount: function(instance,
                               search_query=undefined,
                               types = [],
                               types_method_and = true,
                               hashtags = [],
                               hashtags_method_and = true,
                               activity_mode_filter="all",
                               start_date=undefined,
                               end_date=undefined,
                               pagination_offset=0,
                               pagination_limit=10) {

        let query_params_str = this.getSearchActivitiesQueryParams(search_query, types, types_method_and, hashtags,
            hashtags_method_and, activity_mode_filter, start_date, end_date, pagination_offset, pagination_limit);

        return instance.get('/activities/count?' + query_params_str);
    },

    /**
     * Returns a string representing all the query parameters used for the API call. This function can also be used to set
     * the router's url query for a search page to store the query in browser history.
     * @param search_query String name matching text for an activity
     * @param types List of types an activity must match with, either or/and based on types_method_and
     * @param types_method_and Boolean if types must matched by conjunction or disjunction. True if conjunction.
     * @param hashtags List of hashtags that the activities must match with, either or/and based on hashtags_method_and
     * @param hashtags_method_and Boolean if hashtags must be matched by conjunction or disjunction. True if conjunction.
     * @param activity_mode_filter String for activity mode filtering must match "all", "continuous", "duration"
     * @param start_date Date if activity_mode_filter is "Duration" then this filters any activities that start before this Date.
     * @param end_date Date if activity_mode_filter is "Duration" then this filters any activities that end after this Date.
     * @param pagination_offset Integer offsets search results
     * @param pagination_limit Integer limits number of results returned
     */
    getSearchActivitiesQueryParams: function(search_query=undefined,
                                   types = [],
                                   types_method_and = true,
                                   hashtags = [],
                                   hashtags_method_and = true,
                                   activity_mode_filter="all",
                                   start_date=undefined,
                                   end_date=undefined,
                                   pagination_offset=0,
                                   pagination_limit=10) {
        // Build dictionary of query parameters
        let params = {
            'name':search_query,
            'types':types,
            'types-method':types_method_and ? "AND" : "OR",
            'hashtags': hashtags,
            'hashtags-method': hashtags_method_and ? "AND" : "OR",
            'time': activity_mode_filter,
            'start-date': start_date,
            'end-date': end_date,
            'offset': pagination_offset,
            'limit': pagination_limit
        };

        // Check parameters are valid
        if(params['time'] !== "all" || params['time'] !== "continuous" || params['time'] !== "duration") {
            throw "activity_mode_filter is of invalid string, must be 'continuous', 'duration', 'all'"
        }

        // Cull Irrelevant parameters
        if (params['name'] === undefined) delete params['name'];
        if (params['start_date'] === undefined) delete params['start_date'];
        if (params['end_date'] === undefined) delete params['end_date'];

        if(params['types'].length === 0) {
            delete params['types'];
            delete params['types-method'];
        }

        if(params['hashtags'].length === 0) {
            delete params['hashtags'];
            delete params['hashtags-method'];
        }

        if(params['time'] === 'all') {
            delete params['time'];
        }

        // Construct params into valid string for url
        let query_url_params = new URLSearchParams(params);
        return query_url_params.toString();
    },

    /**
     * Extracts parameters from query url returns an associative array. This is useful for loading in parameters
     * from a page related to activity search
     * @param queryURL query section of a url
     */
    getSearchActivtyParamsFromQueryURL: function (queryURL) {
        let data = {};
        let params = new URLSearchParams(queryURL);

        if (params.has('name')) data['search_query'] = params.get('name');
        if (params.has('types')) data['types'] = params.get('types').split(',');
        if (params.has('types-method')) data['types_method_and'] = params.get('types-method') === "AND";
        if (params.has('hashtags')) data['hashtags'] = params.get('hashtags').split(',');
        if (params.has('hashtags-method')) data['hashtags_method_and'] = params.get('hashtags-method') === "AND";
        if (params.has('time')) data['activity_mode_filter'] = params.get('time');
        else data['activity_mode_filter'] = "all";
        if (params.has('start-date')) data['start_date'] = Date(params.get('start_date'));
        if (params.has('end-date')) data['end_date'] = Date(params.get('end_date'));
        if (params.has('offset')) data['pagination_offset'] = parseInt(params.get('pagination_offset'));
        if (params.has('limit')) data['pagination_limit'] = parseInt(params.get('pagination_limit'));

        return data;
    }
}