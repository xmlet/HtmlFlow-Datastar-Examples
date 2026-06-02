import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { browserHistory as history } from 'react-router';

require('./styles/less/petclinic.less');

import Root from './Root';

const mountPoint = document.getElementById('mount');
ReactDOM.render(
  <Root history={history}/>,
  mountPoint
);
