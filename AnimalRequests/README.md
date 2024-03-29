# Animal Requests

This is a single page app that uses [React Final Form](https://github.com/final-form/react-final-form) along with [TypeScript](https://github.com/Microsoft/TypeScript) and LabKey's [JavaScript API](https://www.labkey.org/_webdav/Documentation/%40files/reference/javascript-api/index.html). The form allows a user to submit a request for animals.

Below is an example of one of the dropdown select fields within the form, followed by a brief explanation of each of the [props](https://reactjs.org/docs/components-and-props.html).

```
<Field
    name="project"
    className="col-xs-5 form-control-input"
    component="select"
    validate={required}
    >
    <option/>
    <option value="TBD">TBD</option>
    <DropdownOptions name="animal_requests_active_projects" rowkey="project"/>
</Field>
```

`Field` is a component imported from react-final-form.

The `className` prop in the `Field` component is the class of field element, using bootstrap grid.

The `name` prop is the name of the column in the insert DB.

The `component` prop describes to react-final-form what type of `Field` it is.

The `validate` prop takes a function that takes the field value, all the values of the form and the meta data about the field and returns an error if the value is invalid, or undefined if the value is valid.

`DropdownOptions` is a component that produces a list of select options given `name` and `rowkey` props.

The `name` prop in the `DropdownOptions` component is the name of the variable that holds the data in the react state.

The `rowkey` prop is the key of the data row returned by LabKey server.

More information about how the entire form works can be found in the documentation linked above. Also, another nice resource provided by react-final-form is its extensive [interactive examples](https://github.com/final-form/react-final-form#examples).

## Release Notes

### 1.2.0

#### Features
* Automatically open 2 secure message board threads (1 "private", 1 "restricted") for each animal request

### 1.1.1 (bundled with v1.2)

#### Features
* For marmosets, hide origin, MHC and viral status fields on insert form
* Removed date needed field
* Added comments field
* Added ability to navigate from a single animal request back to the animal request tab
* Added "On Hold" QC status to dropdown (already in DB)
* Tracking animal assignment rowids in the assignments database (already in DB)
* Allow entry of dead animals and duplicate ids without having to "force submit" into a completed state

### 1.1.0
(2020-11-23)

#### Bug Fixes
* Removed validation for editing `date` 60 days in the past or more

#### Features
* Added new fields:
  * Pregnant animals required
  * Anticipated Start Date
    * added validation to check that this comes after 'date needed' field.
    * also made sure the end date comes after the start date
  * Anticipated End date
  * Executive Committee Approval
  * Animal Ids to Offer (for the project)
    * added validation (split ids, duplicates, alive animals)
* Updated to webpack 4, ts-loader 6
* Autopopulate account field when a project is selected
