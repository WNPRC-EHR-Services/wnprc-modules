// React
import * as React from 'react';
import * as ReactDom from 'react-dom';

// Components
import EnterWeightFormContainer from "./containers/Forms/EnterWeightFormContainer";
import {ContextProvider} from "./containers/App/ContextProvider";
jQuery(() => {
    ReactDom.render(
      <ContextProvider>
                <EnterWeightFormContainer />
      </ContextProvider>,
        document.getElementById('app')
    );
});
