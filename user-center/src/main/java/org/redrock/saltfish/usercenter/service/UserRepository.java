package org.redrock.saltfish.usercenter.service;

import com.google.gson.JsonObject;
import org.redrock.saltfish.common.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public String getDetailedUserInfo(String openid) {
        String[] bindInfo = jdbcTemplate.query(
                "select verify, type from bind where openid = ? and status = ?",
                preparedStatement -> {
                    preparedStatement.setString(1, openid);
                    preparedStatement.setInt(2, 1);
                },
                resultSet -> {
                    if (!resultSet.next()) return null;
                    return new String[]{resultSet.getString("verify"), resultSet.getString("type")};
                });
        if (bindInfo != null) {
            String result;
            if (bindInfo[1].equalsIgnoreCase("student"))
                result = getStudentInfo(bindInfo[0]);
            else if (bindInfo[1].equalsIgnoreCase("graduate"))
                result = getGraduteInfo(bindInfo[0]);
            else
                result = getTeacherInfo(bindInfo[0]);
            return result;
        }
        return null;
    }

    private String getTeacherInfo(String cardId) {
        return jdbcTemplate.query(
                "select name, dept, sex, idnum, cardid from teacher where cardid = ? and status = 1",
                preparedStatement -> {
                    preparedStatement.setString(1, cardId);
                },
                resultSet -> {
                    if (resultSet.next()) {
                        JsonObject result = new JsonObject();
                        result.addProperty("name", resultSet.getString("name"));
                        result.addProperty("dept", resultSet.getString("dept"));
                        result.addProperty("idnum", resultSet.getString("idnum"));
                        result.addProperty("cardid", resultSet.getString("cardid"));
                        return result.toString();
                    }
                    return null;
                });
    }

    private String getGraduteInfo(String stunum) {
        return jdbcTemplate.query(
                "select name, college, sex, stunum, idnum, cardid from gradute where stunum = ? and status = 1",
                preparedStatement -> {
                    preparedStatement.setString(1, stunum);
                },
                resultSet -> {
                    if (resultSet.next()) {
                        JsonObject result = new JsonObject();
                        result.addProperty("name", resultSet.getString("name"));
                        result.addProperty("college", resultSet.getString("college"));
                        result.addProperty("stunum", resultSet.getInt("stunum"));
                        result.addProperty("idnum", resultSet.getString("idnum"));
                        result.addProperty("cardid", resultSet.getString("cardid"));
                        return result.toString();
                    }
                    return null;
                });
    }

    private String getStudentInfo(String stunum) {
        return jdbcTemplate.query(
                "select name, college, major, class, sex, stunum, idnum, cardid from student where stunum = ? and status = 1",
                preparedStatement -> {
                    preparedStatement.setString(1, stunum);
                },
                resultSet -> {
                    if (resultSet.next()) {
                        JsonObject result = new JsonObject();
                        result.addProperty("name", resultSet.getString("name"));
                        result.addProperty("college", resultSet.getString("college"));
                        result.addProperty("major", resultSet.getString("major"));
                        result.addProperty("class", resultSet.getString("class"));
                        result.addProperty("stunum", resultSet.getInt("stunum"));
                        result.addProperty("idnum", resultSet.getString("idnum"));
                        result.addProperty("cardid", resultSet.getString("cardid"));
                        return result.toString();
                    }
                    return null;
                });
    }
    
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StringUtil stringUtil;
}
