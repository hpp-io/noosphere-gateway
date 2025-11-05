const {ModuleFederationPlugin} = require('@module-federation/enhanced/webpack');

const packageJson = require('../package.json');
// Microfrontend api, should match across gateway and microservices.
const apiVersion = '1.0.0';

const sharedDefaults = {singleton: true, strictVersion: false, requiredVersion: false};
const shareMappings = (...mappings) => Object.fromEntries(
    mappings.map(map => [map, {...sharedDefaults, version: apiVersion}]));


// Define packages that should be excluded from sharing to avoid conflicts
const excludeFromSharing = [
  '@reown/appkit',
  '@reown/appkit-adapter-wagmi',
  '@tanstack/react-query',
  'viem',
  'wagmi',
  '@wagmi/core',
  // Add any other wallet-related packages that cause issues
];


const shareDependencies = ({ skipList = [] } = {}) => {
  const allSkipList = [...skipList, ...excludeFromSharing];

  return Object.fromEntries(
      Object.entries(packageJson.dependencies)
      .filter(([dependency]) => !allSkipList.includes(dependency))
      .map(([dependency, version]) => [dependency, { ...sharedDefaults, version }]),
  );
};


module.exports = () => {
  return {
    optimization: {
      moduleIds: 'named',
      chunkIds: 'named',
      runtimeChunk: false,
    },

    plugins: [
      new ModuleFederationPlugin({
        shareScope: 'default',
        dts: false,
        manifest: false,
        shared: {
          // Only share safe, stable dependencies
          'react': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies.react,
          },
          'react-dom': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies['react-dom'],
          },
          'react-router': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies['react-router'],
          },
          'react-router-dom': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies['react-router-dom'],
          },
          'redux': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies.redux,
          },
          'react-redux': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies['react-redux'],
          },
          '@reduxjs/toolkit': {
            singleton: true,
            strictVersion: false,
            requiredVersion: packageJson.dependencies['@reduxjs/toolkit'],
          },
          // Share other safe dependencies
          ...shareDependencies(),
          ...shareMappings(
              'app/config/constants',
              'app/config/store',
              'app/shared/error/error-boundary-routes',
              'app/shared/layout/menus/menu-components',
              'app/shared/layout/menus/menu-item',
              'app/shared/reducers',
              'app/shared/reducers/locale',
              'app/shared/reducers/reducer.utils',
              'app/shared/util/date-utils',
              'app/shared/util/entity-utils',
          ),
        },
      }),
    ],
    output: {
      publicPath: 'auto',
      scriptType: 'text/javascript',
    },
  };
};