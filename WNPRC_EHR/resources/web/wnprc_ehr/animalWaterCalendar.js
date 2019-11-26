Ext4.namespace('EHR.Reports');

EHR.reports.animalWaterCalendar = function (panel, tab){

    var animalIds = [];
    if (tab.filters.subjects){
        tab.filters.subjects.forEach (animalId => animalIds.push(animalId));
        renderCalendar (animalIds, tab);
    }

    function renderCalendar(animalIds, tab){
        if (!animalIds.length){
            tab.add({
                html: 'No animal selected or found with water restrictions',
                border: false
            })
        }

        if (animalIds.length > 0){
            var waterCalendar = new LABKEY.WebPart({
                partName: 'Water Calendar',
                renderTo: 'panel-1326-innerCt'
            });
            waterCalendar.render();

        }


    }

    console.log ("new render");
    animalIds.forEach(id => console.log (id));

}