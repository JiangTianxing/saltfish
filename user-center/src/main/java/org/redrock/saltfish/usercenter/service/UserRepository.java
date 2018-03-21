package org.redrock.saltfish.usercenter.service;

import com.google.gson.JsonObject;
import org.redrock.saltfish.common.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

    /**
     * 格式为 type:openid:json
     * 例如      student:oiL6j0e16ZpAXBKLIprkQ43fPtgk:{"name" : "jiangtianxing", "sex" : 1}
     * @param openid
     * @return
     */
    public Map<String, String> getDetailedUserInfo(String openid) {
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
        Map<String, String> data = new HashMap<>();
        if (bindInfo != null) {
            String result;
            if (bindInfo[1].equalsIgnoreCase("student"))
                result = getStudentInfo(bindInfo[0]);
            else if (bindInfo[1].equalsIgnoreCase("graduate"))
                result = getGraduateInfo(bindInfo[0]);
            else
                result = getTeacherInfo(bindInfo[0]);
            data.put("type", bindInfo[1]);
            data.put("data", result);
        } else {
            data.put("type", "tourist");
            data.put("data", null);
        }
        return data;
    }

    private String getTeacherInfo(String cardId) {
        return jdbcTemplate.query(
                "select name, dept, sex, idnum, cardid from teacher where cardid = ?",
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
                        result.addProperty("type", "teacher");
                        return result.toString();
                    }
                    return null;
                });
    }

    private String getGraduateInfo(String stunum) {
        return jdbcTemplate.query(
                "select name, college, sex, stunum, idnum, cardid from gradute where stunum = ?",
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
                        result.addProperty("type", "graduate");
                        return result.toString();
                    }
                    return null;
                });
    }

    private String getStudentInfo(String stunum) {
        return jdbcTemplate.query(
                "select name, college, major, class, sex, stunum, idnum, cardid from student where stunum = ?",
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
                        result.addProperty("type", "student");
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
