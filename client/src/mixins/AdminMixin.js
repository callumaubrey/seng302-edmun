import axios from "axios";


export default {
    data: function () {
        return {}
    },
    methods: {
        getUserRoles: function () {
            return axios.get("http://localhost:9499/profiles/role")
                .then(function (response) {
                    return response.data;
                })
        },
        checkUserIsAdmin: function () {
            return this.getUserRoles()
                .then((roles) => {
                    if (roles) {
                        for (let i = 0; i < roles.length; i++) {
                            if (roles[i].roleName === "ROLE_ADMIN" || roles[i].roleName === "ROLE_USER_ADMIN") {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .catch((err) => {
                    console.log(err);
                    return null;
                })

        }
    }
};