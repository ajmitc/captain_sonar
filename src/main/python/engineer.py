from gamemap import Gamemap
import random

class Engineering( object ):
	MINE_TORPEDO	 = "Mine/Torpedo"
	DRONE_SONAR	  = "Drone/Silence"
	SILENCE_SCENARIO = "Silence/Scenario"
	REACTOR		  = "Reactor"
	
	def __init__( self ):
		self.components = [
		# type, compartment, chain, damaged 
			[ self.MINE_TORPEDO,     Gamemap.WEST,  1, False ],
			[ self.SILENCE_SCENARIO, Gamemap.WEST,  1, False ],
			[ self.DRONE_SONAR,      Gamemap.WEST,  1, False ],
			[ self.DRONE_SONAR,      Gamemap.WEST,  0, False ],
			[ self.REACTOR,          Gamemap.WEST,  0, False ],
			[ self.REACTOR,          Gamemap.WEST,  0, False ],
			
			[ self.SILENCE_SCENARIO, Gamemap.NORTH, 2, False ],
			[ self.SILENCE_SCENARIO, Gamemap.NORTH, 2, False ],
			[ self.MINE_TORPEDO,     Gamemap.NORTH, 2, False ],
			[ self.DRONE_SONAR,      Gamemap.NORTH, 0, False ],
			[ self.MINE_TORPEDO,     Gamemap.NORTH, 0, False ],
			[ self.REACTOR,          Gamemap.NORTH, 0, False ],
			
			[ self.DRONE_SONAR,      Gamemap.SOUTH, 3, False ],
			[ self.SILENCE_SCENARIO, Gamemap.SOUTH, 3, False ],
			[ self.MINE_TORPEDO,     Gamemap.SOUTH, 3, False ],
			[ self.MINE_TORPEDO,     Gamemap.SOUTH, 0, False ],
			[ self.REACTOR,          Gamemap.SOUTH, 0, False ],
			[ self.SILENCE_SCENARIO, Gamemap.SOUTH, 0, False ],
			
			[ self.DRONE_SONAR,      Gamemap.EAST,  2, False ],
			[ self.SILENCE_SCENARIO, Gamemap.EAST,  3, False ],
			[ self.MINE_TORPEDO,     Gamemap.EAST,  1, False ],
			[ self.REACTOR,          Gamemap.EAST,  0, False ],
			[ self.DRONE_SONAR,      Gamemap.EAST,  0, False ],
			[ self.REACTOR,          Gamemap.EAST,  0, False ]
		]
		
		self.chains = []
		for i in xrange( 4 ):
			chain = []
			for l in self.components:
				if l[ 2 ] == i:
					chain.append( l )
			self.chains.append( chain )
			
			
	def damage( self, component ):
		""" Damage a component.  Return true if all components damaged in same compartment of the sub.  Return false otherwise. """
		component[ 3 ] = True
		# check if sub damaged or chain repaired
		subdamaged = True
		chain_repaired = True
		for comp in self.components:
			if comp[ 1 ] == component[ 1 ]:
				if not comp[ 3 ]:
					subdamaged = False
			if comp[ 2 ] == component[ 2 ]: # smae chain
				if not comp[ 3 ]:
					chain_repaired = False
		if chain_repaired:
			for comp in self.components:
				if comp[ 2 ] == component[ 2 ]: # same chain
					comp[ 3 ] = False
		return subdamaged
	
	
	def get_random_undamaged_component( self, dir ):
		undamaged = [ c for c in self.components if c[ 1 ] == dir and not c[ 3 ] ]
		return random.choice( undamaged )
	
	