Ext4.namespace('EHR.Reports');

EHR.reports.animalWaterCalendar = function (panel, tab){

    var animalIds = [];
    if (tab.filters.subjects){
        // tab.filters.subjects.forEach (animalId => animalIds+=animalId+';');
        // animalIds = animalIds.substring(0, animalIds.length-1);
        animalIds = tab.filters.subjects;
        renderCalendar (animalIds, tab);
    }
    else{
        panel.resolveSubjectsFromHousing(tab,renderCalendar,this);

    }



    function renderCalendar(animalIds, tab){
        if (!animalIds.length){
            tab.add({
                html: 'No animal selected or found with water restrictions',
                border: false
            })
        }
        debugger;
        var concatAnimals = "";

        if (animalIds.length > 0){
            animalIds.forEach (animalId => concatAnimals+=animalId+';');
            concatAnimals = concatAnimals.substring(0, concatAnimals.length-1);
            tab.add({
                xtype: 'panel',
                id: 'waterCal'

            })
            var waterCalendar = new LABKEY.WebPart({
                partName: 'Water Calendar',
                renderTo: 'panel-1326-innerCt',
                //renderTo: 'waterCal',
                partConfig: {animalIds : concatAnimals}
            });
            waterCalendar.render();

        }


    }

    console.log ("new render");
    //animalIds.forEach(id => console.log (id));

}