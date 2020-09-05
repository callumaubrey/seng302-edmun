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
    searchActivities: function (instance,
        search_query = null,
        types = [],
        types_method_and = true,
        hashtags = [],
        hashtags_method_and = true,
        activity_mode_filter = "all",
        start_date = null,
        end_date = null,
        pagination_offset = 0,
        pagination_limit = 10,
        longitude = null,
        latitude = null,
        radius = null
    ) {

        let query_params_str = this.getSearchActivitiesQueryParams(search_query,
            types, types_method_and, hashtags,
            hashtags_method_and, activity_mode_filter, start_date, end_date,
            pagination_offset, pagination_limit,
            longitude, latitude, radius);
        query_params_str = query_params_str.replace(/%2C/g, '%20');

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
     */
    searchActivitiesPageCount: function (instance,
        search_query = null,
        types = [],
        types_method_and = true,
        hashtags = [],
        hashtags_method_and = true,
        activity_mode_filter = "all",
        start_date = null,
        end_date = null,
        longitude = null,
        latitude = null,
        radius = null) {

        let query_params_str = this.getSearchActivitiesQueryParams(search_query,
            types, types_method_and, hashtags,
            hashtags_method_and, activity_mode_filter, start_date, end_date,
            null, null,
            longitude, latitude, radius);
        query_params_str = query_params_str.replace(/%2C/g, '%20');

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
    getSearchActivitiesQueryParams: function (search_query = null,
        types = [],
        types_method_and = true,
        hashtags = [],
        hashtags_method_and = true,
        activity_mode_filter = "all",
        start_date = null,
        end_date = null,
        pagination_offset = 0,
        pagination_limit = 10,
        longitude = null,
        latitude = null,
        radius = null) {
        // Build dictionary of query parameters
        let params = {
            'name': search_query,
            'types': types,
            'types-method': types_method_and ? "AND" : "OR",
            'hashtags': hashtags,
            'hashtags-method': hashtags_method_and ? "AND" : "OR",
            'time': activity_mode_filter,
            'start-date': start_date,
            'end-date': end_date,
            'offset': pagination_offset,
            'limit': pagination_limit,
            'lon': longitude,
            'lat': latitude,
            'radius': radius
        };

        // Check parameters are valid
        if(params['time'] !== "all" && params['time'] !== "continuous" && params['time'] !== "duration") {
            throw "activity_mode_filter is of invalid string, must be 'continuous', 'duration', 'all'"
        }

        // Cull Irrelevant parameters
        if (params['name'] === null || params['name'].length === 0) delete params['name'];
        if (params['start-date'] === null) delete params['start-date'];
        if (params['end-date'] === null) delete params['end-date'];
        if (params['offset'] === null) delete params['offset'];
        if (params['limit'] === null) delete params['limit'];

        if(params['time'] !== "duration") {
            delete params['start-date'];
            delete params['end-date'];
        }

        if(params['types'].length === 0) {
            delete params['types'];
            delete params['types-method'];
        }

        if (params['hashtags'].length === 0) {
            delete params['hashtags'];
            delete params['hashtags-method'];
        }

        if (params['time'] === 'all') {
            delete params['time'];
        }

        if (params['radius'] === null) {
            delete params['radius'];
            delete params['lat'];
            delete params['lon'];
        }

        // Construct params into valid string for url
        let query_url_params = new URLSearchParams(params);
        return query_url_params.toString();
    },

    /**
     * Extracts parameters from query url returns an associative array. This is useful for loading in parameters
     * from a page related to activity search
     * @param query query section of a url
     */
    getSearchActivtyParamsFromQueryURL: function (query) {
        let data = {};
        let params = new URLSearchParams(query);

        if (params.has('name')) {
            data['search_query'] = params.get('name');
        }
        if (params.has('types')) {
            data['types'] = params.get('types').split(',');
        }
        if (params.has('types-method')) {
            data['types_method_and'] = params.get(
                'types-method') === "AND";
        }
        if (params.has('hashtags')) {
            data['hashtags'] = params.get(
                'hashtags').split(',');
        }
        if (params.has(
            'hashtags-method')) {
            data['hashtags_method_and'] = params.get(
                'hashtags-method') === "AND";
        }
        if (params.has('time')) {
            data['activity_mode_filter'] = params.get(
                'time');
        } else {
            data['activity_mode_filter'] = "all";
        }
        if (params.has('start-date')) {
            data['start_date'] = params.get(
                'start-date');
        }
        if (params.has('end-date')) {
            data['end_date'] = params.get('end-date');
        }
        if (params.has('offset')) {
            data['pagination_offset'] = parseInt(
                params.get('offset'));
        }
        if (params.has('limit')) {
            data['pagination_limit'] = parseInt(
                params.get('limit'));
        }
        if (params.has('lon')) {
            data['longitude'] = params.get('lon');
        }
        if (params.has('lat')) {
            data['latitude'] = params.get('lat');
        }
        if (params.has('radius')) {
            data['radius'] = params.get('radius');
        }

        return data;
    }
}