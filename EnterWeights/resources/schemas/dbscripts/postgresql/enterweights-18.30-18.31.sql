insert into ehr.form_framework_types (schemaname,queryname,framework,container) select 'study', 'weight', 'reactjs', container from ehr.form_framework_types where rowid=1;