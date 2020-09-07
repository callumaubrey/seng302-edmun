import activitySearch from "@/scripts/Activity/activitySearch";

describe('activitySearch.js', () => {
  test(
      'Location attributes are in axios query when getSearchActivitiesQueryParams is called',
      async () => {
        // default search activity attributes
        let search_query = null;
        let types = [];
        let types_method_and = true;
        let hashtags = [];
        let hashtags_method_and = true;
        let activity_mode_filter = "all";
        let start_date = null;
        let end_date = null;
        let pagination_offset = 0;
        let pagination_limit = 10;

        // search activity location attributes
        let longitude = 10.23;
        let latitude = 9.83;
        let radius = 10;
        let queryString = activitySearch.getSearchActivitiesQueryParams(
            search_query, types, types_method_and, hashtags,
            hashtags_method_and, activity_mode_filter,
            start_date, end_date, pagination_offset, pagination_limit,
            longitude, latitude, radius);

        expect(queryString.includes('lon=10.23'));
        expect(queryString.includes('lat=9.83'));
        expect(queryString.includes('radius=10'));
      })

  test(
      'Location attributes are not in axios query when radius is null and getSearchActivitiesQueryParams is called',
      async () => {
        // default search activity attributes
        let search_query = null;
        let types = [];
        let types_method_and = true;
        let hashtags = [];
        let hashtags_method_and = true;
        let activity_mode_filter = "all";
        let start_date = null;
        let end_date = null;
        let pagination_offset = 0;
        let pagination_limit = 10;

        // search activity location attributes
        let longitude = 10.23;
        let latitude = 9.83;
        let radius = null;
        let queryString = activitySearch.getSearchActivitiesQueryParams(
            search_query, types, types_method_and, hashtags,
            hashtags_method_and, activity_mode_filter,
            start_date, end_date, pagination_offset, pagination_limit,
            longitude, latitude, radius);

        expect(!queryString.includes('lon=10.23'));
        expect(!queryString.includes('lat=9.83'));
        expect(!queryString.includes('radius=10'));
      })

  test(
      'Location parameters are extracted into array when getSearchActivtyParamsFromQueryURL is called',
      async () => {
        let urlQuery = 'activities/search?name=hike&offset=1&limit=10&lat=10.23&lon=9.83&radius=10'
        let data = activitySearch.getSearchActivtyParamsFromQueryURL(urlQuery);

        expect(data['longitude'] === 10.23);
        expect(data['latitude'] === 9.83);
        expect(data['radius'] === 10);
      })
})