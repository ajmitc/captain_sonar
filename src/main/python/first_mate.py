

class Systems( object ):
	MINE     = "Drop Mine"
	TORPEDO  = "Torpedo"
	SONAR    = "Sonar"
	SILENCE  = "Silence"
	DRONE    = "Drone"
	SCENARIO = "Scenario"
	
	TRIGGER_MINE = "Trigger Mine"
    
	def __init__( self ):
		# [ charges, max charges ]
		self.systems = {
			self.MINE:     [ 0, 3 ],
			self.TORPEDO:  [ 0, 3 ],
			self.SONAR:    [ 0, 3 ],
			self.SILENCE:  [ 0, 6 ],
			self.DRONE:    [ 0, 4 ],
			self.SCENARIO: [ 0, 4 ]  # or 6
		}
        
        
	def charge( self, what ):
		if what in self.systems.keys():
			if self.systems[ what ][ 0 ] < self.systems[ what ][ 1 ]:
				self.systems[ what ][ 0 ] += 1
    
    
	def is_charged( self, what ):
		if what in self.systems.keys():
			return self.systems[ what ][ 0 ] == self.systems[ what ][ 1 ]
		return False
	
	def reset( self, what ):
		if what in self.systems.keys():
			self.systems[ what ][ 0 ] = 0
        