/* For upgrading from flash-based ciknow */
/* Because this script cannot be run on new ciknow database, the upgrade process has to be done manually*/

ALTER TABLE surveys MODIFY name VARCHAR(255) NOT NULL;



ALTER TABLE questions MODIFY survey_id BIGINT(20) NULL;
ALTER TABLE questions MODIFY type VARCHAR(255) NOT NULL;
ALTER TABLE questions MODIFY shortName VARCHAR(255) NOT NULL;
ALTER TABLE questions MODIFY label VARCHAR(255) NOT NULL;

ALTER TABLE question_fields DROP PRIMARY KEY;
ALTER TABLE question_fields ADD PRIMARY KEY(field_id);
ALTER TABLE question_fields MODIFY field_id BIGINT(20) NOT NULL DEFAULT NULL AUTO_INCREMENT;
ALTER TABLE question_fields MODIFY version BIGINT(20) NOT NULL;
ALTER TABLE question_fields MODIFY sequence_number INT(11) NULL;

ALTER TABLE question_scales DROP PRIMARY KEY;
ALTER TABLE question_scales ADD PRIMARY KEY(scale_id);
ALTER TABLE question_scales MODIFY scale_id BIGINT(20) NOT NULL DEFAULT NULL AUTO_INCREMENT;
ALTER TABLE question_scales MODIFY version BIGINT(20) NOT NULL;
ALTER TABLE question_scales MODIFY sequence_number INT(11) NULL;

ALTER TABLE question_text_fields DROP PRIMARY KEY;
ALTER TABLE question_text_fields ADD PRIMARY KEY(text_field_id);
ALTER TABLE question_text_fields MODIFY text_field_id BIGINT(20) NOT NULL DEFAULT NULL AUTO_INCREMENT;
ALTER TABLE question_text_fields MODIFY version BIGINT(20) NOT NULL;
ALTER TABLE question_text_fields MODIFY sequence_number INT(11) NULL;

ALTER TABLE question_contact_fields DROP PRIMARY KEY;
ALTER TABLE question_contact_fields ADD PRIMARY KEY(contact_field_id);
ALTER TABLE question_contact_fields MODIFY contact_field_id BIGINT(20) NOT NULL DEFAULT NULL AUTO_INCREMENT;
ALTER TABLE question_contact_fields MODIFY version BIGINT(20) NOT NULL;
ALTER TABLE question_contact_fields MODIFY sequence_number INT(11) NULL;

DROP TABLE IF EXISTS question_option;



ALTER TABLE nodes MODIFY type VARCHAR(255) NOT NULL;
ALTER TABLE nodes MODIFY username VARCHAR(255) NOT NULL;
ALTER TABLE nodes MODIFY password VARCHAR(255) NOT NULL;
ALTER TABLE nodes MODIFY first_name VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY last_name VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY mid_name VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY addr1 VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY addr2 VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY city VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY state VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY country VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY zipcode VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY email VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY phone VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY cell VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY fax VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY department VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY organization VARCHAR(255) DEFAULT NULL;
ALTER TABLE nodes MODIFY unit VARCHAR(255) DEFAULT NULL;


ALTER TABLE edges MODIFY weight double NOT NULL;


ALTER TABLE roles MODIFY name VARCHAR(255) NOT NULL;


ALTER TABLE groups MODIFY name VARCHAR(255) NOT NULL;


ALTER TABLE activities MODIFY predicate VARCHAR(255) NOT NULL;