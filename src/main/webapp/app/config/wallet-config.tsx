import { WagmiAdapter } from '@reown/appkit-adapter-wagmi';
import { mainnet, arbitrum, sepolia } from '@reown/appkit/networks';
import type { AppKitNetwork } from '@reown/appkit/networks';

export const projectId = process.env.HPP_APP_KIT_PROJECT_ID;

if (!projectId) {
  throw new Error('Project ID is not defined');
}

export const metadata = {
  name: 'Noosphere',
  description: 'Noosphere',
  url: 'https://www.hpp.io',
  icons: ['https://avatars.githubusercontent.com/u/179229932'],
};
const env = process.env.HPP_ENV || 'development';
export const networks = (env === 'production' ? [mainnet] : [sepolia]) as [AppKitNetwork, ...AppKitNetwork[]];

export const wagmiAdapter = new WagmiAdapter({
  projectId,
  networks,
});

export const config = wagmiAdapter.wagmiConfig;
