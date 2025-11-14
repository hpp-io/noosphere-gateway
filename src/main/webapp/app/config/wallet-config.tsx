import { WagmiAdapter } from '@reown/appkit-adapter-wagmi';
import { mainnet, arbitrum, sepolia } from '@reown/appkit/networks';
import type { AppKitNetwork } from '@reown/appkit/networks';

export const metadata = {
  name: 'Noosphere',
  description: 'Noosphere',
  url: 'https://www.hpp.io',
  icons: ['https://avatars.githubusercontent.com/u/179229932'],
};

export function initializeWagmi(projectId: string, envValue: string) {
  if (!projectId) {
    throw new Error('Project ID is not defined');
  }

  const networks = (envValue === 'production' ? [mainnet] : [sepolia]) as [AppKitNetwork, ...AppKitNetwork[]];

  const wagmiAdapter = new WagmiAdapter({
    projectId,
    networks,
  });

  return { wagmiAdapter, config: wagmiAdapter.wagmiConfig };
}
