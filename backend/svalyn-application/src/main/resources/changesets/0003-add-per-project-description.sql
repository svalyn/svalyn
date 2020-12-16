ALTER TABLE Description ADD COLUMN projectId UUID NOT NULL;
ALTER TABLE Description ADD CONSTRAINT description_projectId_fk FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE;
ALTER TABLE Description DROP CONSTRAINT description_label_unique;