insert into ehr.form_framework_types (schemaname,queryname,framework,container) select 'study', 'feeding', 'reactjs', entityid from core.containers WHERE name='EHRa' LIMIT 1;