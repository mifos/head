/*
 *  Copyright 2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.mifos.application.master.persistence;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.customers.surveys.business.Question;

import java.util.Iterator;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Loads all question records and forces Hibernate to do update
 * to compute nickname column value.
 */
public class Upgrade1288013750 extends Upgrade {

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        SqlUpgrade upgrade1 = SqlUpgradeScriptFinder.findUpgradeScript(
                "upgrade1288013750_step1.sql");
        upgrade1.runScript(connection);

        try {
            Session session = StaticHibernateUtil.getSessionTL();
            Query query = session.createQuery("from Question");
            Iterator it = query.iterate();
            while (it.hasNext()) {
                Question question = (Question)it.next();
                String questionText = question.getQuestionText();
                if (questionText == null) {
                    questionText = "x";
                }
                int questionId = question.getQuestionId();
                boolean updateResults = updateQuestion(session, questionText, questionId);
                while (!updateResults) {
                    questionText = questionText + "x";
                    updateResults = updateQuestion(session, questionText, questionId);
                }
            }
        }
        catch (Exception e) {
            SqlUpgrade revertUpgrade = SqlUpgradeScriptFinder.findUpgradeScript(
                    "upgrade1288013750_revert_step1.sql");
            revertUpgrade.runScript(connection);
            throw new IOException(e);
        }

        SqlUpgrade upgrade2 = SqlUpgradeScriptFinder.findUpgradeScript(
                "upgrade1288013750_step2.sql");
        upgrade2.runScript(connection);
    }

    private boolean updateQuestion(Session session, String questionText, int questionId) throws UnsupportedEncodingException {
        Query update = session.createQuery("update Question set nickname = :nickname where question_id = :id");
        String nickname;
        try {
            nickname = computeMD5(questionText);
            update.setString("nickname", nickname);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        update.setInteger("id", questionId);
        update.executeUpdate();

        Query query = session.createQuery("from Question where nickname = :nickname");
        query.setString("nickname", nickname);
        List results = query.list();

        return !(results != null && results.size() > 1);
    }


    private String computeMD5(String questionText) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        md.update(questionText.getBytes("utf-8"), 0, questionText.length());
        return convertToHex(md.digest());
    }

    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

}
